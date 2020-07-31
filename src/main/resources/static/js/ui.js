let ui;

class Player {
	constructor(playerInfo) {
		this.id = null;
		this.username = null;
		this.cardsInHand = null;
		this.seat = null;

		if (playerInfo) this.fillInfo(playerInfo);
	}

	fillInfo(playerInfo) {
		this.id = playerInfo.id;
		this.username = playerInfo.username;
	}
}

class BurracoUI {
	constructor(web_socket) {
		ui = this;
		this.hand = [];
		this.numPlayers = gameInfo["numPlayers"];
		this.discardPile = [];
		this.webSocket = web_socket;
		this.turnPhase = "NOPE";
		this.myRuns = [];
		this.otherRuns = [];

		this.players = [];
		gameInfo["players"].forEach(function (pInfo, i) {
			ui.players[i] = new Player(pInfo);
		});

		this.setup_events();

		//display_other_hand("west", 11);
		//display_other_hand("east", 11);
	}

	setup_events() {
		$("#discard").on("mouseenter", ui.discard_open)
			.on("mouseleave", ui.discard_close);

		$("#main").on("dragover", function(e) {e.preventDefault();})
			.on("drop", function() {$(".moving").removeClass("moving")});

		$("#points-button").on("mouseenter", function() {
			$("#points").show();
		});
		$("#points").on("mouseleave", function() {
			$("#points").hide();
		});

		$("#chat-button").on("mouseenter", function() {
			$("#chat").show();
			$("#chat-button").removeClass("chat-new-msg");

		});
		$("#chat").on("mouseleave", function() {
			$("#chat").hide();
		});

		$("#chat-send").on("click", ui.action_send_msg);

		$( "#chat-msg > input" ).on("keypress", function( event ) {
			if ( event.key == 'Enter' ) {
				ui.action_send_msg();
			}
		});

		$("#south").on("click", ".card", function () {
			console.log("click card: "+$(this).index());
			$(this).toggleClass("selected-card").removeClass("moving")
		})
			.on("mousedown", ".card", function (e) {$(this).addClass("moving");})
			.on("drop", ".card", function (e) {
				e.preventDefault();
				ui.move_card_hand($(this).index());
			});
	}

	startGame() {
		this.set_deck(108-1-22-11*this.numPlayers, false);
		for (let i=0; i<this.players.length; i++) {
			if (i!==this.id) {
				this.set_other_hand(this.players[i], 11, false);
			}
		}
		$("#my-runs").html("");
		$("#other-runs").html("");
		this.myRuns = [];
		this.otherRuns = [];

		// display_run("#my-runs", ["0", "1C,2C,3C,4C,5C,6C,7C".split(","), "NONE"]);
		// display_run("#my-runs", ["1", "1C,1C,1C,1C".split(","), "NONE"]);
	}

	somebody_joined(who_id, playerInfo) {
		let player = this.players[who_id];
		player.fillInfo(playerInfo);
		display_badge(player);
		display_chat_msg("Info", player.username+" just joined.");
	}

	somebody_abandoned(who_id) {
		let player = this.players[who_id];
		remove_badge(player);
		display_chat_msg("Info", player.username+" abandoned the game.");
		$("#chat").show();
	}

	set_turn(t) {
		switch (t) {
			case "TAKE":
				$("#deck").on("click", ui.action_draw);
				$("#discard").on("click", ui.action_pick);
				break;
			case "DISCARD":
				$("#deck").off('click');
				$("#discard").off('click').on("drop click", function(e) {
					e.preventDefault();
					ui.action_discard();
				});
				$("#my-runs").on("drop click", function(e){
					if(e.target != this) return;
					ui.action_new_meld();
				});
				break;
			case "NOPE":
				$("#discard").off('drop click');
				$("#my-runs").off('click');
				break;
		}
		this.turnPhase = t;
	}

	set_id(id) {
		this.id = id;
		this.players[id].seat = "south";
		let nextId = (parseInt(id)+1)%2;
		if (this.numPlayers==2) {
			this.players[nextId.toString()].seat = "north";
		} else { // 4 players
			this.players[nextId.toString()].seat = "west";
			nextId = (nextId+1)%2;
			this.players[nextId.toString()].seat = "north";
			nextId = (nextId+1)%2;
			this.players[nextId.toString()].seat = "east";
		}

		gameInfo["players"].forEach(function (pInfo, i) {
			if(pInfo != null) display_badge(ui.players[i]);
		});
	}

	set_discard_pile(cards) {
		this.discardPile = cards;
		display_discard(this.discardPile);
	}

	set_deck(num, is_relative) {
		if (is_relative) this.cardsInDeck += num;
		else this.cardsInDeck = num;
		display_deck(this.cardsInDeck);
	}

	set_hand(hand) {
		this.hand = hand;
		display_hand(this.hand);
	}

	move_card_hand(dst) {
		let mov = $(".moving");
		if (mov.length==0) return;
		let src = mov.index();
		if(src!=dst) {
			ui.hand.splice(dst, 0, ui.hand.splice(src, 1)[0]);
		}
		mov.removeClass("moving");
		ui.set_hand(ui.hand);
	}

	action_draw() {
		let msg = ui.create_msg("DRAW", null, null);
		ui.webSocket.send(msg);
	}

	action_pick() {
		let msg = ui.create_msg("PICK", null, null);
		ui.webSocket.send(msg);
	}

	action_new_meld() {
		let to_meld = [];
		$(".selected-card, .moving").each(function(i) {
			to_meld.push($(this).attr("data-value"));
		});
		if (to_meld.length==0) return;
		let msg = ui.create_msg("MELD", null, "-1;"+to_meld);
		ui.webSocket.send(msg);
		$(".moving").removeClass("moving");
	}

	action_old_meld() {
		let to_meld = [];
		$(".selected-card, .moving").each(function(i) {
			to_meld.push($(this).attr("data-value"));
		});
		if (to_meld.length==0) return;
		let run_ix = $(this).attr("data-index");
		let msg = ui.create_msg("MELD", null, run_ix+";"+to_meld);
		ui.webSocket.send(msg);
		$(".moving").removeClass("moving");
	}

	action_discard() {
		let card = $(".moving");
		if (card.length==0) {
			card = $(".selected-card");
			if (card.length!=1) return;
		}
		card.addClass("to-remove");
		card.removeClass("moving");
		let msg = ui.create_msg("DISCARD", null, card.attr("data-value"));
		ui.webSocket.send(msg);
	}

	action_send_msg() {
		let input = $("#chat-msg > input");
		let msg = ui.create_msg("CHAT", ui.id, input.val());
		ui.webSocket.send(msg);
		input.val("");
	}

	draw_card(card) {
		this.set_hand(this.hand.concat([card]));
	}

	other_draw_card(who_id) { // other as in "other player"
		let player = this.players[who_id];
		this.set_other_hand(player, 1, true);
		this.set_deck(-1, true);
	}

	pick(who_id) {
		if (who_id==this.id) {
			this.set_hand(this.hand.concat(this.discardPile));
		} else {
			let player = this.players[who_id];
			this.set_other_hand(player, this.discardPile.length, true);
		}
		this.set_discard_pile([]);
	}

	discard(who_id, card) {
		this.discardPile.push(card);
		display_discard(this.discardPile);
		if (who_id==this.id) {
			this.hand_remove([card]);
		} else {
			let player = this.players[who_id];
			this.set_other_hand(player, -1, true);
		}
	}

	set_run(who, run) {
		let where = "#my-runs";
		if (Math.abs(who-this.id)==1) where = "#other-runs";

		let run_div = display_run(where, run);

		if (where == "#my-runs") {
			ui.myRuns[run[0]] = run[1];
			run_div.off("drop click").on("drop click", ui.action_old_meld);
		} else {
			ui.otherRuns[run[0]] = run[1];
		}
	}

	pot_taken(who_id) {
		let player = this.players[who_id];
		this.set_other_hand(player, 11, false);
	}

	discard_open() {
		$("#discard > .discard-half").removeClass("discard-half").addClass("run-half");
	}

	discard_close() {
		$("#discard > .run-half").removeClass("run-half").addClass("discard-half");
	}

	set_other_hand(player, num, is_relative) {
		if (is_relative) player.cardsInHand += num;
		else player.cardsInHand = num;
		display_other_hand(player);
	}

	hand_remove(to_remove) {
		$(".selected-card, .to-remove").each(function(i) {
			let card_value = $(this).attr("data-value");
			let pos = to_remove.indexOf(card_value);
			if (pos>-1) {
				to_remove.splice(pos, 1);
				let pos_hand = $(this).index();//ui.hand.indexOf(card_value);
				ui.hand.splice(pos_hand, 1);
			}
		});
		to_remove.forEach(function(item, index) {
			let pos_hand = ui.hand.indexOf(item);
			ui.hand.splice(pos_hand, 1);
		});
		ui.set_hand(ui.hand);
	}

	meld_hand_remove(who_id, run_ix, cards) {
		if (who_id != this.id) {
			let to_remove = cards.length;
			if (ui.otherRuns.length > run_ix) {
				to_remove = cards.length-ui.otherRuns[run_ix].length;
			}
			let player = this.players[who_id];
			this.set_other_hand(player, -to_remove, true);
		} else {
			let cards_cp = JSON.parse(JSON.stringify(cards)) // deep copy
			if (ui.myRuns.length > run_ix) {
				ui.myRuns[run_ix].forEach(function(item) {
					cards_cp.splice(cards_cp.indexOf(item), 1);
				});
			}
			ui.hand_remove(cards_cp);
		}
	}

	create_msg(type, sender, content) {
		return JSON.stringify({"type":type, "sender":sender, "content":content});
	}
}

$(function() {
// display_run("#my-runs", "1;CLEAN;1D,2D,3D,4D,5D,6D,7D");
// display_run("#my-runs", "2;SEMICLEAN;1D,2D,3D,4D,5D,6D,7D");
// display_run("#my-runs", "3;DIRTY;1D,2D,3D,4D,5D,6D,7D");
//
// display_hand();
// display_other_hand("north", 5);
// display_other_hand("west", 5);
// display_other_hand("east", 5);
//
// discard_display("10C,11C,12C,13C");
// $("#discard").on("mouseenter", discard_open);
// $("#discard").on("mouseleave", discard_close);
//
// deck_display(42);
});