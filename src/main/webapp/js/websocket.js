
$(function() {
    $.getJSON('/games/'+window.location.pathname, main);
});

function main(game_info) {
    let endpoint = "ws://"+window.location.hostname+":8080"+game_info["id"];
    let playerId = -1;
    let webSocket = new WebSocket(endpoint);
    let burracoUI = new BurracoUI(game_info["numPlayers"], webSocket.send);
    // TODO: use game_info["seatsToAssign"] in some way

    webSocket.onopen = function(event) {
        console.log('onopen::' + JSON.stringify(event, null, 4));
    }

    webSocket.onmessage = function(event) {
        let msg = JSON.parse(event.data);
        console.log('onmessage::' + JSON.stringify(msg, null, 4));
        switch (msg["type"]) {
            case "JOIN":
                if (msg["sender"]=="Player") {
                    playerId = parseInt(msg["content"]);
                    burracoUI.set_id(playerId);
                    // display stuff
                } else {
                    // display other stuff
                }
                break;
            case "START_ROUND":
                let discard = decode_cardset(msg["content"])[0];
                burracoUI.set_discardPile(discard);
                burracoUI.startGame();
                break;
            case "HAND":
                let hand = decode_cardset(msg["content"])[0];
                burracoUI.set_hand(hand);
                break;
            case "TURN":
                // display stuff and timer
                break;
            case "DRAW":
                if (msg["sender"]=="Player") {
                    let card = msg["content"].replace("|", "");
                    burracoUI.draw_card(card);
                } else if (msg["content"]!=playerId){
                    burracoUI.other_draw_card(msg["content"]);
                }
                break;
            case "PICK":
                let who = msg["content"];
                if (who==playerId){
                    burracoUI.pick();
                } else {
                    burracoUI.other_pick(who);
                }
                break;
            case "MELD":
                let run = decode_run(msg["content"]);
                burracoUI.display_run(msg["sender"], run);
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
                burracoUI.display_chat(msg["sender"], msg["content"]);
                break;
        }
    }

    webSocket.onclose = function(event) {
        console.log('onclose::' + JSON.stringify(event, null, 4));
    }

    webSocket.onerror = function(event) {
        console.log('onerror::' + JSON.stringify(event, null, 4));
    }

}

function decode_cardset(cs) {
    cs = cs.split(";");
    let burType = cs[1];
    let cards = cs[0].split("|").join("").split(",");
    return [cards, burType];
}

function decode_run(run) {
    let [index, cards, bur_type] = run.split(";");
    cards = cards.split("|").join("").split(",");
    return [index, cards, bur_type];
}