const CARD_WIDTH = 77;
const HALF_CARD_WIDTH = 20;

function card(css_class, value) {
	return '<div class='+css_class+'><img src="/cards/'+value+'.jpg" data-value="'+value+'"></div>';
}

function display_deck(num_cards) {
	let to_display = Math.ceil(num_cards/5);
	let result = card("card", "back");
	for (let i=1; i<to_display; i++) {
		result += card("deck-half", "back");
	}
	$("#deck").html(result);
}

function display_discard(cards) {
	let result = "";
	let discard_len = cards.length;
	let discard_div = $("#discard");
	if (discard_len > 0) {
		let i;
		for (i = 0; i < discard_len-1; i++) {
			result += card("discard-half", cards[i]);
		}
		result += card("card", cards[i]);
		discard_div.removeClass("border");
	} else {
		discard_div.addClass("border");
	}
	discard_div.html(result);
}

function display_hand(cards) { // TODO: check if can be merged with display_other_hand
	let hand_html = '';
	let per_row = 1+ Math.floor(($("#south").width()-CARD_WIDTH)/HALF_CARD_WIDTH);
	let south_div = $("#south");
	let cards_len = cards.length;

	if (cards_len>per_row) south_div.addClass("south-double-row");
	else south_div.removeClass("south-double-row")

	for (let i = 0; i<cards_len; i++) {
		if ((i+1)%per_row==0 || i==cards.length-1) {
			hand_html += card("my-hand", cards[i]);
		} else {
			hand_html += card("my-hand-half", cards[i]);
		}
	}
	south_div.html(hand_html);


}

function display_other_hand(where, num_cards) {
	if (where=="north") {
		let hand = '';
		for (let i=0; i<num_cards-1; i++) {
			hand += card("north-hand-half", "back");
		}
		hand += card("north-hand", "back");
		$("#north").html(hand);
	} else if (where=="west") {
		let hand = '';
		for (let i=0; i<num_cards-1; i++) {
			hand += card("west-hand-half", "back");
		}
		hand += card("west-hand", "back");
		$("#west").html(hand);
	} else if (where=="east") {
		let hand = '';
		for (let i=0; i<num_cards-1; i++) {
			hand += card("east-hand-half", "back");
		}
		hand += card("east-hand", "back");
		$("#east").html(hand);
	}
}

function display_run(where, run) {
	let [run_index, cards, bur_type] = run;
	let run_div = $(where+' > div[data-index="'+run_index+'"]');
	if (!run_div.length) {
		$(where).append('<div class="run column" data-index="'+run_index+'"></div>');
		run_div = $(where+' > div[data-index="'+run_index+'"]');
	}
	let run_html = "";
	let c_class;
	for (let i = cards.length-1; i>=0; i--) {
		if (i==1 && (bur_type=="CLEAN" || bur_type=="SEMICLEAN")) {
			c_class = "run-horiz-overflow";
		} else if(i==0 && (bur_type=="CLEAN" || bur_type=="DIRTY")) {
			c_class = "run-horiz";
		} else if(i==0 && (bur_type=="NONE" || bur_type=="SEMICLEAN")) {
			c_class = "card";
		} else {
			c_class = "run-half";
		}
		run_html += card(c_class, cards[i]);
	}
	run_div.html(run_html);
	return run_div;
}

function display_chat(who, msg) {
	// TODO
}

