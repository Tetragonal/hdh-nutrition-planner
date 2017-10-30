
//library/imported stuff
var options = {
			valueNames: ['name', 'price', 'diningHall', 'calories', 'fat', 'cholesterol', 'sodium', 'carbohydrates', 'fiber', 'protein', 'allergens']
	}
, documentTable = new List('mdl-table', options)
;

$($('th.sort')[0]).trigger('click', function () {
	console.log('clicked');
});

$('input.search').on('keyup', function (e) {
	if (e.keyCode === 27) {
		$(e.currentTarget).val('');
		documentTable.search('');
	}
});

//listener for restaurant search dialog
(function() {
    'use strict';
    var dialogButton = document.querySelector('.dialog-button');
    var dialog = document.querySelector('#dialog');
    if (!dialog.showModal) {
        dialogPolyfill.registerDialog(dialog);
    }
    dialogButton.addEventListener('click', function() {
        dialog.showModal();
    });
    dialog.querySelector('button:not([disabled])')
        .addEventListener('click', function() {
            dialog.close();
        });
}());

//listener for update database dialog
(function() {
    'use strict';
    var dialogButton = document.querySelector('.update-dialog');
    var dialog = document.querySelector('#dialog2');
    if (!dialog.showModal) {
        dialogPolyfill.registerDialog(dialog);
    }
    dialogButton.addEventListener('click', function() {
        dialog.showModal();
    });
    dialog.querySelector('#buttonDatabase')
        .addEventListener('click', function() {
            dialog.close();
            alert("hi");
        });
}());

//on loading document
/*
$( document ).ready(function() {
    var dialog = document.querySelector('#dialog');
    
    // get restaurants
    var url = "https://rainbowcat-jetty.herokuapp.com/api";
    url += '?' + $.param({
      'api-key': "c67da996ee9f498d832c961558f5fa09",
      'q': cityStr
    });
      $.getJSON(url, function(data){
      //console.log(data.response.docs);
      $nytHeaderElem.text("New York Times Articles About " + cityStr)
      data.response.docs.forEach(function(element){
        if(element.document_type == "article"){
          $nytElem.append('<li class="article"><a href=' + element.web_url + ' target="_blank">' + element.headline.main + '</a><p>' + element.lead_paragraph + '</p></li>');
        }
      });
    }).fail(function() {
      $nytHeaderElem.text("New York Times Articles Could Not Be Loaded");
    });
    
    
    for(int i=0; i<blah.length; i++){
      //add checkboxes to hidden dialog
    }
});
*/

//database stuff
function updateDatabaseDialog(){
  
}