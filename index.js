var url = "https://rainbowcat-jetty.herokuapp.com/api";


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
    dialog.querySelector('#searchButton')
        .addEventListener('click', function() {
            
            var restaurants = null;
            $.ajax({
              url: url,
              type: "POST",
              dataType: "json",
              data: '{"operation":"restaurants"}'
            })
            .done(function(data){
              restaurants = $.map(data, function(el) {
                if(el != true && el != false){//ignore "success"
                  return el; 
                }});
              //var $wikiElem = $('#wikipedia-links');
              //$wikiElem.append('');
              alert(restaurants);
              //alert(JSON.stringify(data, null, 2));
            })
            .fail(function(){
              alert("Failed to load");
            });
            dialog.close();
    });
    dialog.querySelector('button:not([disabled])')
        .addEventListener('click', function() {
            dialog.close();
        });
    
    //add restaurants
    var restaurants = null;
    $.ajax({
      url: url,
      type: "POST",
      dataType: "json",
      data: '{"operation":"restaurants"}'
    })
    .done(function(data){
      restaurants = data["restaurants"];
      var $restaurantDiv = $('#restaurantDiv');
      for(var i=0; i<restaurants.length; i++){
        //$restaurantDiv.append("<label class=\"mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect\" for=\"checkbox-" + i + "\"><input type=\"checkbox\" id=\"checkbox-" + i + "\" class=\"mdl-checkbox__input\"><span class=\"mdl-checkbox__label\">" + restaurants[i] + "</span></input></label>");
        
        var restaurantDiv = document.getElementById('restaurantDiv');
        
        var label = document.createElement('label');
        label.className = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect restaurantCheckbox";
        label.setAttribute("for", "checkbox-" + i);
        
        var input = document.createElement('input');
        label.appendChild(input);
        input.id = "checkbox-" + i;
        input.className = "mdl-checkbox__input";
        
        var span = document.createElement('span');
        label.appendChild(span);
        span.className = "mdl-checkbox__label";
        
        var textNode = document.createTextNode(restaurants[i]);
        span.appendChild(textNode);
        
        restaurantDiv.appendChild(label);
        
        //$restaurantDiv.append("<br /> <br />");
        
        componentHandler.upgradeAllRegistered();

        /*
        <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-123">
          <input type="checkbox" id="checkbox-123" class="mdl-checkbox__input">
            <span class="mdl-checkbox__label">
            Checkbox
          </span>
        </label>
        */
      }
     $('.mdl-js-checkbox.restaurantCheckbox').each(function (index, element) {
        console.log(element.onclick);
        element.addEventListener('click', function(e){
          if(!element.classList.contains("is-checked")){
            element.MaterialCheckbox.check();
          }else{
            element.MaterialCheckbox.uncheck();
          }
          e.stopPropagation();
          e.preventDefault();
        });
        console.log(element.onclick);
      })
    })
    .fail(function(){
      alert("Failed to load");
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
            updateDatabase();
            
            var lastModified = null;
            $.ajax({
              url: url,
              type: "POST",
              dataType: "json",
              data: '{"operation":"lastModified"}'
            })
            .done(function(data){
              lastModified = $.map(data, function(el) {
                if(el != true && el != false){//ignore "success"
                  return el; 
                }});
              //var $wikiElem = $('#wikipedia-links');
              //$wikiElem.append('');
                //<label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-1">
                //<input type="checkbox" id="checkbox-1" class="mdl-checkbox__input" checked>
                //<span class="mdl-checkbox__label">Checkbox</span>
                //</label>
              //');
              alert(lastModified);
              //alert(JSON.stringify(data, null, 2));
            })
            .fail(function(){
              alert("Failed to load");
            });
            dialog.close();
    });
    dialog.querySelector('button:not([disabled])')
        .addEventListener('click', function() {
            dialog.close();
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
function updateDatabase(){
  
}