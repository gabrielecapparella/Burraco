
$(function() {
	let endpoint = "ws://"+window.location.hostname+":8080/game/"+gameInfo["id"];
	let playerId = -1;
	let burracoUI = null;
	let webSocket = new WebSocket(endpoint);

	webSocket.onopen = function(event) {
		console.log('onopen::' + JSON.stringify(event, null, 4));
		burracoUI = new BurracoUI(webSocket);
	}

	webSocket.onmessage = function(event) {
		let msg = JSON.parse(event.data);
		let who = msg["sender"];
		console.log('onmessage::' + JSON.stringify(msg, null, 4));
		switch (msg["type"]) {
			case "JOIN":
				if (who=="Player") {
					playerId = parseInt(msg["content"]);
					burracoUI.set_id(playerId);
				} else {
					let player = JSON.parse(msg["content"]);
					burracoUI.somebody_joined(parseInt(who), player);
				}
				break;
			case "EXIT":
				burracoUI.somebody_abandoned(who);
				break;
			case "START_ROUND":
				let discard = decode_cardset(msg["content"])[0];
				burracoUI.set_discard_pile(discard);
				burracoUI.startGame();
				break;
			case "HAND": // private
				let hand = decode_cardset(msg["content"])[0];
				burracoUI.set_hand(hand);
				break;
			case "TURN":
				console.log("turn, "+playerId+", "+who); // TODO: why doesn't this get executed???
				if (who==playerId) {
					burracoUI.set_turn(msg["content"]);
				}
				display_turn(burracoUI.players[who].seat, msg["content"]);
				break;
			case "DRAW":
				if (who=="Player") {
					let card = msg["content"];
					burracoUI.draw_card(card);
				} else if (who!=playerId){
					burracoUI.other_draw_card(who);
				}
				break;
			case "PICK":
				burracoUI.pick(who);
				break;
			case "MELD":
				let run = decode_run(msg["content"]);
				burracoUI.meld_hand_remove(who, run[0], run[1]);
				burracoUI.set_run(who, run);
				break;
			case "DISCARD":
				let card = msg["content"];
				burracoUI.discard(who, card);
				break;
			case "POT":
				if (who!==playerId) burracoUI.pot_taken(who);
				display_chat_msg("Info", burracoUI.players[who].username+" took the pot.");
				break;
			case "END_ROUND":
				display_points(JSON.parse(msg["content"]), playerId);
				display_modal("Round finished", null); // TODO
				break;
			case "END_GAME":
				display_points(JSON.parse(msg["content"]), playerId);
				display_modal("Game finished", null); // TODO
				break;
			case "CHAT":
				let name = burracoUI.players[who].username;
				if (name == 'undefined') name = "Info";
				display_chat_msg(name, msg["content"]);
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
	let cards = cs[0].split(",");
	return [cards, burType];
}

function decode_run(run) {
	let [index, cards, bur_type] = run.split(";");
	cards = cards.split(",");
	return [index, cards, bur_type];
}