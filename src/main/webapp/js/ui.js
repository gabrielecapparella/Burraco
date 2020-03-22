const CARD_WIDTH = 70;
const HALF_CARD_WIDTH = 20;

function display_run(where, run) { // where= "#my-runs" || "#other-runs"
	let [index, bur_type, cards] = run.split(";");
	let run_div = $(where+' > div[data-index="'+index+'"]');
	if (!run_div.length) {
		$(where).append('<div class="run column" data-index="'+index+'"></div>');
		run_div = $(where+' > div[data-index="'+index+'"]');
	}
	cards = cards.split(',');
	let run_html = "";
	let c_class;
	for (i = cards.length-1; i>=0; i--) {
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

function deck_display(num_cards) {
	let to_draw = Math.floor(num_cards/5)+1;
	let result = '<div class="card"><img src="/cards/back.jpg"></div>';
	for (i=1; i<to_draw; i++) {
		result += '<div class="half-deck"><img src="/cards/back.jpg"></div>';
	}
	$("#deck").html(result);
}

function discard_display(cards) {
	cards = cards.split(",");
	let result = "";
	for (i=0; i<cards.length-1; i++) {
		result += '<div class="half-discard"><img src="/cards/'+cards[i]+'.jpg"></div>';
	}
	result += '<div class="card"><img src="/cards/'+cards[i]+'.jpg"></div>';
	$("#discard").html(result);
}

function discard_open() {
	$("#discard > .half-discard").removeClass("half-discard").addClass("half-card");
}

function discard_close() {
	$("#discard > .half-card").removeClass("half-card").addClass("half-discard");
}

function display_hand() {
	let hand_html = '';
	let per_row = 1+ Math.floor(($("#south").width()-CARD_WIDTH)/HALF_CARD_WIDTH);
	let card_class;
	for (i = 0; i<hand.length; i++) {
		if ((i+1)%per_row==0 || i==hand.length-1) {
			card_class = "hand-card";
		} else {
			card_class = "hand-half";
		}
		hand_html += '<div class="'+card_class+'"><img src="/cards/'+hand[i]+'.jpg"></div>';
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
			hand.splice(dst, 0, hand.splice(src, 1)[0]);
			display_hand();
		}
	});

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

let hand = "1S,2S,3S,4S,5S,6S,7S,8S,9S,10S,11S,12S,13S,1S".split(",");

$(function() {
	display_run("#my-runs", "1;CLEAN;1D,2D,3D,4D,5D,6D,7D");
	display_run("#my-runs", "2;SEMICLEAN;1D,2D,3D,4D,5D,6D,7D");
	display_run("#my-runs", "3;DIRTY;1D,2D,3D,4D,5D,6D,7D");

	display_hand();
	display_other_hand("north", 5);
	display_other_hand("west", 5);
	display_other_hand("east", 5);

	discard_display("10C,11C,12C,13C");
	$("#discard").on("mouseenter", discard_open);
	$("#discard").on("mouseleave", discard_close);

	deck_display(42);
});