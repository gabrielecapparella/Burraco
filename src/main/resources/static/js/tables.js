$(function() {
	$.getJSON('/games', display_tables);
	$("#create-table").on("click", form_create_game);
});

function display_tables(tables) {
	let tbody_html = "";
	let taken_seats;
	for (let i=0; i<tables.length; i++) {
		taken_seats = tables[i]["numPlayers"]-tables[i]["seatsToAssign"];
		tbody_html += '<tr>';
		tbody_html += '<td>'+taken_seats+"/"+tables[i]["numPlayers"]+'</td>';
		tbody_html += '<td>'+tables[i]["targetPoints"]+'</td>';
		tbody_html += '</tr>';
	}
	$("#tables-table  tbody").html(tbody_html);
}

function form_create_game() {
	let gameInfo = {
		"targetPoints": $("input[name='targetPoints']:checked").val(),
		"numPlayers": $("input[name='numPlayers']:checked").val()
	}
	$.ajax({
		type: 'POST',
		url: '/games',
		data: JSON.stringify (gameInfo),
		success: function(data) {
			$(location).attr('href', data)
			},
		contentType: "application/json",
	});
}
