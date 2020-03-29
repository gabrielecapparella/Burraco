
$(function(){
    let endpoint = "ws://"+window.location.hostname+":8080"+window.location.pathname;
    let webSocket = new WebSocket(endpoint);


    webSocket.onopen = function(event) {
        console.log('onopen::' + JSON.stringify(event, null, 4));
    }

    webSocket.onmessage = function(event) {
        let msg = JSON.parse(event.data);
        console.log('onmessage::' + JSON.stringify(msg, null, 4));
        console.log(typeof(msg));
        console.log(msg["type"]=="HAND");
        switch (msg["type"]) {
            case "JOIN":
                break;
            case "HAND":
                let hand = decode_cardset(msg["content"])[0];
                display_hand(hand);
                break;
            case "TURN":
                break;
            case "DRAW":
                break;
            case "PICK":
                break;
            case "MELD":
                break;
            case "DISCARD":
                break;
            case "POT":
                break;
            case "END_ROUND":
                break;
            case "END_GAME":
                break;
            case "CHAT":
                break;
        }
    }

    webSocket.onclose = function(event) {
        console.log('onclose::' + JSON.stringify(event, null, 4));
    }

    webSocket.onerror = function(event) {
        console.log('onerror::' + JSON.stringify(event, null, 4));
    }

});

function decode_cardset(cs) {
    cs = cs.split(";");
    let burType = cs[1];
    let cards = cs[0].split("|").join("").split(",");
    return [cards, burType];
}