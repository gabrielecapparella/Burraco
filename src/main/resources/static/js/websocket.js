
$(function() {
	let endpoint = "ws://"+window.location.hostname+":8080/game/"+gameInfo["id"];
	let playerId = -1;
	let burracoUI = null;
	let webSocket = new WebSocket(endpoint);

	// TODO: use game_info["seatsToAssign"] in some way

	webSocket.onopen = function(event) {
		console.log('onopen::' + JSON.stringify(event, null, 4));
		burracoUI = new BurracoUI(webSocket);
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
				} else {
					burracoUI.somebody_joined(parseInt(msg["content"]));
				}
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
				who = msg["sender"];
				if (who==playerId) {
					burracoUI.set_turn(msg["content"]);
				}
				display_turn(burracoUI.player2seat[who], msg["content"]);
				break;
			case "DRAW":
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
				who = msg["sender"];
				let run = decode_run(msg["content"]);
				burracoUI.meld_hand_remove(who, run[0], run[1]);
				burracoUI.set_run(who, run);
				break;
			case "DISCARD":
				let card = msg["content"];
				burracoUI.discard(msg["sender"], card);
				break;
			case "POT":
				if (msg["sender"]!=playerId) burracoUI.pot_taken(msg["sender"]);
				display_chat_msg("Info", burracoUI.player2name[msg["sender"]]+" took the pot.");
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
				let name = burracoUI.player2name[msg["sender"]];
				if (name === 'undefined') name = "Info";
				display_chat_msg(name, msg["content"]);
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