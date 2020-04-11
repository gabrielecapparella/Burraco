const CARD_WIDTH = 70;
const HALF_CARD_WIDTH = 20;

class BurracoUI {
	constructor(numPlayers, web_socket) {
		this.hand = [];
		this.numPlayers = numPlayers;
		this.cardsInOtherHands = [];
		this.discardPile = [];
		this.player2seat = [];
		this.webSocket = web_socket;
		this.turnPhase = "NOPE";

		$("#discard").on("mouseenter", this.discard_open);
		$("#discard").on("mouseleave", this.discard_close);
	}

	startGame() {
		this.cardsInDeck = 108-1-11*this.numPlayers;
		this.display_deck()
		for (let p in this.player2seat) {
			if (p!=this.id) {
				this.cardsInOtherHands[p] = 11;
			}
		}
	}

	set_turn(t) {
		// TODO: set triggers
		switch (t) {
			case "TAKE":
				$("#deck").on("click", this.action_draw.bind(this));
				$("#discard").on("click", this.action_pick);
				break;
			case "DISCARD":
				break;
			case "NOPE":
				break;
		}
		this.turnPhase = t;
	}

	set_id(id) {
		this.id = id;
		this.player2seat[id] = "south";
		let nextId = (id+1)%2;
		if (this.numPlayers==2) {
			this.player2seat[nextId] = "north";
		} else { // 4 players
			this.player2seat[nextId] = "west";
			nextId = (nextId+1)%2;
			this.player2seat[nextId] = "north";
			nextId = (nextId+1)%2;
			this.player2seat[nextId] = "east";
		}
	}

	set_discardPile(discard) {
		this.discardPile = discard;
		this.display_discard();
	}

	set_hand(hand) {
		this.hand = hand;
		this.display_hand();
	}

	action_draw() { // TODO
		alert("draw_card");
		let msg = JSON.stringify({"type":"DRAW", "sender":null, "content":null});
		this.webSocket.send(msg);
	}

	action_pick() { // TODO
		alert("pick_card");
	}

	draw_card(card) {
		this.hand.push(card);
		this.display_hand();
	}

	other_draw_card(who_id) { // other as in "other player"
		this.cardsInOtherHands[who_id] += 1;
		this.display_other_hand(who_id);
	}

	pick(who_id) {
		if (who_id==this.id) {
			this.hand.concat(this.discardPile);
			this.display_hand();
		} else {
			this.cardsInOtherHands[who_id] += this.discardPile.length;
			this.display_other_hand(who_id);
		}
		this.discardPile = [];
	}

	discard(who_id, card) {
		this.discardPile.add(card);
		this.display_discard();
		if (who_id==this.id) {
			this.hand_remove([card]);
		} else {
			this.cardsInOtherHands[who_id] -= 1;
			this.display_other_hand(who_id);
		}
	}

	display_run(who_id, run_index, cards, bur_type) {
		if (who_id!=this.id) {
			this.cardsInOtherHands[who_id] -= cards.length;
			this.display_other_hand(who_id);
		}

		let where = "#my-runs";
		if (Math.abs(who_id-this.id)==1) where = "#other-runs";

		let run_div = $(where+' > div[data-index="'+run_index+'"]');
		if (!run_div.length) {
			$(where).append('<div class="run column" data-index="'+run_index+'"></div>');
			run_div = $(where+' > div[data-index="'+run_index+'"]');
		}
		let run_html = "";
		let c_class;
		for (let i = cards.length-1; i>=0; i--) {
			if (i==1 && (bur_type=="CLEAN" || bur_type=="SEMICLEAN")) {
				c_class = "half-horiz-overflow";
			} else if(i==0 && (bur_type=="CLEAN" || bur_type=="DIRTY")) {
				c_class = "horiz";
			} else if(i==0 && (bur_type=="NONE" || bur_type=="SEMICLEAN")) {
				c_class = "card";
			} else {
				c_class = "half-card";
			}
			run_html += '<div class="'+c_class+'"><img src="/cards/'+cards[i]+'.jpg"></div>';
		}
		run_div.html(run_html);
	}

	display_deck() {
		let to_display = Math.floor(this.cardsInDeck/5)+1;
		let result = '<div class="card"><img src="/cards/back.jpg"></div>';
		for (let i=1; i<to_display; i++) {
			result += '<div class="half-deck"><img src="/cards/back.jpg"></div>';
		}
		$("#deck").html(result);
	}

	display_discard() {
		let result = "";
		let i;
		for (i=0; i<this.discardPile.length-1; i++) {
			result += '<div class="half-discard"><img src="/cards/'+this.discardPile[i]+'.jpg"></div>';
		}
		result += '<div class="card"><img src="/cards/'+this.discardPile[i]+'.jpg"></div>';
		$("#discard").html(result);

	}

	discard_open() {
		$("#discard > .half-discard").removeClass("half-discard").addClass("half-card");
	}

	discard_close() {
		$("#discard > .half-card").removeClass("half-card").addClass("half-discard");
	}

	display_hand() { // TODO: check if can be merged with display_other_hand
		let parent = this;
		let hand_html = '';
		let per_row = 1+ Math.floor(($("#south").width()-CARD_WIDTH)/HALF_CARD_WIDTH);
		let card_class;
		for (let i = 0; i<this.hand.length; i++) {
			if ((i+1)%per_row==0 || i==this.hand.length-1) {
				card_class = "hand-card";
			} else {
				card_class = "hand-half";
			}
			hand_html += '<div class="'+card_class+'"><img src="/cards/'+this.hand[i]+'.jpg"></div>';
		}
		$("#south").html(hand_html);

		$("#south img").on("click", function () {$(this).toggleClass("selected-card");});
		$("#south img").on("mousedown", function (e) {$(this).addClass("moving");});
		$("#south img").on("dragover", function(e) {e.preventDefault();});
		$("#south img").on("drop", function (e) {
			e.preventDefault();
			let mov = $(".moving");
			if (mov.length==0) return;
			let src = mov.parent().index();
			let dst = $(this).parent().index();
			if(src!=dst) {
				parent.hand.splice(dst, 0, parent.hand.splice(src, 1)[0]);
				parent.display_hand();
			}
		});
	}

	display_other_hand(who_id) {
		let num_cards = this.cardsInOtherHands[who_id];
		let seat = this.player2seat[who_id];
		if (seat=="north") {
			let hand = '';
			for (let i=0; i<num_cards-1; i++) {
				hand += '<div class="hand-half hand-back"><img src="/cards/back.jpg"></div>';
			}
			hand += '<div class="hand-card hand-back"><img src="/cards/back.jpg"></div>';
			$("#north").html(hand);
		} else if (seat=="west") {
			let hand = '';
			for (let i=0; i<num_cards-1; i++) {
				hand += '<div class="hand-half-back-horiz hand-west"><img src="/cards/back.jpg"></div>';
			}
			hand += '<div class="hand-back-horiz hand-west"><img src="/cards/back.jpg"></div>';
			$("#west").html(hand);
		} else if (seat=="east") {
			let hand = '';
			for (let i=0; i<num_cards-1; i++) {
				hand += '<div class="hand-half-back-horiz"><img src="/cards/back.jpg"></div>';
			}
			hand += '<div class="hand-back-horiz"><img src="/cards/back.jpg"></div>';
			$("#east").html(hand);
		}
	}

	hand_remove(cards) {
		// TODO: remove cards from hand preferring the ones highlighted
	}

	display_chat(who, msg) {
		// TODO
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