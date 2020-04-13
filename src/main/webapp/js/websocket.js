
$(function() {
	let game_id = window.location.pathname.split("/")[2];
	$.getJSON('/games/gamerepo/'+game_id, main);
});

function main(game_info) {
	let endpoint = "ws://"+window.location.hostname+":8080/game/"+game_info["id"];
	let playerId = -1;
	let burracoUI = null;
	let webSocket = new WebSocket(endpoint);

	// TODO: use game_info["seatsToAssign"] in some way

	webSocket.onopen = function(event) {
		console.log('onopen::' + JSON.stringify(event, null, 4));
		burracoUI = new BurracoUI(game_info["numPlayers"], webSocket);
	}

	webSocket.onmessage = function(event) {
		let msg = JSON.parse(event.data);
		let who; // TODO: msg.sender should always be a player_id, not just "Player" or "Game"
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
			case "HAND": // private
				let hand = decode_cardset(msg["content"])[0];
				burracoUI.set_hand(hand);
				break;
			case "TURN":
				if (msg["sender"]==playerId) {
					burracoUI.set_turn(msg["content"]);
				}
				break;
			case "DRAW":
				burracoUI.cardsInDeck -= 1;
				burracoUI.display_deck();
				if (msg["sender"]=="Player") {
					let card = msg["content"];
					burracoUI.draw_card(card);
				} else if (msg["content"]!=playerId){
					burracoUI.other_draw_card(msg["content"]);
				}
				break;
			case "PICK":
				who = msg["content"];
				burracoUI.pick(who);
				break;
			case "MELD":
				let run = decode_run(msg["content"]);
				if (msg["sender"]==playerId) burracoUI.meld_hand_remove(run[0], run[1]);
				burracoUI.display_run(msg["sender"], run);
				break;
			case "DISCARD":
				let card = msg["content"];
				burracoUI.discard(msg["sender"], card);
				break;
			case "POT":
				if (msg["sender"]!=playerId) burracoUI.pot_taken(msg["sender"]);
				break;
			case "END_ROUND":
				// TODO
				break;
			case "END_GAME":
				// TODO
				break;
			case "CHAT":
				burracoUI.display_chat(msg["sender"], msg["content"]);
				break;
		}
	}

	webSocket.onclose = function(event) {
		console.log('onclose::' + JSON.stringify(event, null, 4));
		// TODO
	}

	webSocket.onerror = function(event) {
		console.log('onerror::' + JSON.stringify(event, null, 4));
	}

}

function decode_cardset(cs) {
	cs = cs.split(";");
	let burType = cs[1];
	let cards = cs[0].split(",");
	return [cards, burType];
}

function decode_run(run) {
	let [index, cards, bur_type] = run.split(";");
	cards = cards.split(",");
	return [index, cards, bur_type];
}