const CARD_WIDTH = 70;
const HALF_CARD_WIDTH = 20;

function display_run(where, index, cards) { // where= "#my-runs" || "#other-runs"
	let run = $(where+' > div[data-index="'+index+'"]');
	if (!run.length) {
		$(where).append('<div class="run column" data-index="'+index+'"></div>');
		run = $(where+' > div[data-index="'+index+'"]');
	}
	cards = cards.split(',');
	let run_html = "";
	for (i = cards.length-1; i>0; i--) {
		run_html += '<div class="half-card"><img src="/cards/'+cards[i]+'.jpg"></div>';
	}
	run_html += '<div class="card"><img src="/cards/'+cards[i]+'.jpg"></div>';
	run.html(run_html);
}

function display_hand(cards) {
	cards = cards.split(',');
	let hand = '';
	let per_row = 1+ Math.floor(($("#south").width()-CARD_WIDTH)/HALF_CARD_WIDTH);
	let card_class;
	for (i = 0; i<cards.length; i++) {
		if ((i+1)%per_row==0 || i==cards.length-1) {
			card_class = "hand-card";
		} else {
			card_class = "hand-half";
		}
		hand += '<div class="'+card_class+'"><img src="/cards/'+cards[i]+'.jpg"></div>';
	}
	$("#south").html(hand);
}

function display_other_hand(whom, num_cards) {
	if (whom=="north") {
		let hand = '';
		for (i=0; i<num_cards-1; i++) {
			hand += '<div class="hand-half hand-back"><img src="/cards/back.jpg"></div>';
		}
		hand += '<div class="hand-card hand-back"><img src="/cards/back.jpg"></div>';
		$("#north").html(hand);
	} else if (whom=="west") {
		let hand = '';
		for (i=0; i<num_cards-1; i++) {
			hand += '<div class="hand-half-back-horiz hand-west"><img src="/cards/back.jpg"></div>';
		}
		hand += '<div class="hand-back-horiz hand-west"><img src="/cards/back.jpg"></div>';
		$("#west").html(hand);
	} else if (whom=="east") {
		let hand = '';
		for (i=0; i<num_cards-1; i++) {
			hand += '<div class="hand-half-back-horiz"><img src="/cards/back.jpg"></div>';
		}
		hand += '<div class="hand-back-horiz"><img src="/cards/back.jpg"></div>';
		$("#east").html(hand);
	}
}

$(function() {
	// display_run("#my-runs", 0, "1D,2D,3D");
	display_hand("1D,2D,3D,2D,3D,2D,3D,2D,3D,2D,3D,2D,3D,2D,3D,2D,3D,2D,3D,2D,3D");
	display_other_hand("north", 5);
	display_other_hand("west", 5);
	display_other_hand("east", 5);
});