$(function() {
    
    // global ajax settings to supply auth token to all requests
    $.ajaxSetup({
        beforeSend: function(xhr, settings) {
            var token = sessionStorage.getItem('token');
            if (token) {
                xhr.setRequestHeader('X-co-auth-token', token);
            }
        }
    });

    if (!sessionStorage.getItem("token") || !sessionStorage.getItem("rootId") || !sessionStorage.getItem('coApiUrl')) {
        $("#loginContainer").css('display','block');
    } else {
        $("#uploadContainer").css('display','block');
        startToListenWS();
    }

    // handle login button
    $("#loginForm").on("submit", function(ev){
        ev.preventDefault();
        var formData = $( this ).serialize();
        $.post({
            url: "/api/login", 
            data: formData 
        })
        .then(function(loginData) {
            addToStream("#messageTpl", {message: "Login Succeeded, Token="+loginData.token});
            
            sessionStorage.setItem('token', loginData.token);
            sessionStorage.setItem('coApiUrl', loginData.coApiUrl);
            
            startToListenWS();
            
            return $.get(loginData.coApiUrl+"/api/v1/auth/profile");
        })
        .then(function(profile) {
            addToStream("#messageTpl", {message: "Logged in as "+profile.login});

            sessionStorage.setItem('login', profile.login);

            return $.get(sessionStorage.getItem('coApiUrl')+"/api/v1/info");
        })
        .then(function(info){
            var rootId = jsonPath(info, "$.info.roots[?(@.filename=='.root')].id");
            
            sessionStorage.setItem('rootId', rootId);
            
            addToStream("#messageTpl", {message: "Root folder ID: "+rootId});
            
            $("#loginContainer").css('display','none');
            $("#uploadContainer").css('display','block');
        })
        .fail(function(xhr, status, error) {
            addToStream("#errTpl", {err: xhr.responseText});
            sessionStorage.clean();
        });
    });

    
    // handle upload button
    $("#uploadForm").on("submit", function(ev){
        ev.preventDefault();
        $( this ).ajaxSubmit({
            url: sessionStorage.getItem('coApiUrl')+"/api/v1/files/upload",
            method: 'POST',
            clearForm: true,
            data: {
                'synchronous': true,
                'conflictStrategy': 'keep_both',
                'metadata': '{"parentId":"' + sessionStorage.getItem('rootId') +'"}'
            },
            success: function(data) {
                var link = jsonPath(data, "$.links[?(@.rel=='link')].href");
                var frameOpenButton = $('<input class="openFrameButton" type="submit" value="Open in iFrame" />');
                frameOpenButton.data({link: link});
                addToStream("#uploadTpl", {link: link}, frameOpenButton);
            },
            error: function(xhr, status, error) {
                addToStream("#errTpl", {err: xhr.responseText});
            }
        });
    });

    $("#eventStream").on("click", "input.openFrameButton", function(ev){
        ev.preventDefault();
        $("#overlay").css("display","block");
        $("#overlay iframe").attr("src", $(this).data('link'));
    });

//    $("#eventStream").on("submit", "form.exportForm", function(ev){
//        $(this).ajaxSubmit();
//        return false;
//    });

    $("#closeFrameButton").click(function(ev){
        ev.preventDefault();
        $("#overlay iframe").attr("src", "about:blank");
        $("#overlay").css("display","none");
    });

    $("#clearButton").click(function(ev){
        ev.preventDefault();
        $("#eventStream").empty();
    });
    
});


function startToListenWS() {
    var host = $('<a>').attr('href', sessionStorage.getItem('coApiUrl')).prop('hostname');
    var wssUrl= 'wss://'+host+'/api/v1/files/subscribe?X-co-auth-token='+sessionStorage.getItem('token');
    
    var socket = new WebSocket(wssUrl);
    addToStream("#messageTpl", {message: "Started to listen client's WS notifications"});
    
    addToStream("#wsTpl",{message: 'Socket Status: '+socket.readyState});

    socket.onopen = function(){
        addToStream("#wsTpl",{message:'Socket Status: '+socket.readyState+' (open)'});
    }

    socket.onmessage = function(msg){
        var eventString = msg.data.replace(/^\d+\|/, '').trim();
        if (eventString) {
            addToStream("#wsTpl",{message: 'Received: '+eventString});
            var event = JSON.parse(eventString);
            var exportForm = $($.templates("#exportTpl").render({
                apiUrl: sessionStorage.getItem('coApiUrl'),
                objectId: event.objectId,
                token: sessionStorage.getItem('token')
            }));
            if (event.operation === "DOCUMENT_CLOSE_SUCCESSED") {
                addToStream("#messageTpl", {message: "File '"+event.objectId+"' has been closed in the cloud. "}, exportForm);
            } else if (event.operation === "DOCUMENT_SAVE_SUCCESSED") {
                addToStream("#messageTpl", {message: "File '"+event.objectId+"' has been saved in the cloud. "}, exportForm);
            }
        }
    }

    socket.onclose = function(){
        addToStream("#wsTpl",{message: 'Socket Status: '+socket.readyState+' (Closed)'});
    }
}

function addToStream(template, obj, child) {
    var elem = $($.templates(template).render(obj));
    if (child) {
        elem.append(child);
    }
    
    $("#eventStream").prepend(elem);
}
