$(function(){
    let endpoint = "ws://"+window.location.hostname+":8080"+window.location.pathname;
    let webSocket = new WebSocket(endpoint);


    webSocket.onopen = function(event) {
        console.log('onopen::' + JSON.stringify(event, null, 4));
    }

    webSocket.onmessage = function(event) {
        let msg = event.data;
        console.log('onmessage::' + JSON.stringify(msg, null, 4));
    }

    webSocket.onclose = function(event) {
        console.log('onclose::' + JSON.stringify(event, null, 4));
    }

    webSocket.onerror = function(event) {
        console.log('onerror::' + JSON.stringify(event, null, 4));
    }

});