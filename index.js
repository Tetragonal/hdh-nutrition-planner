var url = "https://rainbowcat-jetty.herokuapp.com/api";
var handler;
var mdlComp;

//library/imported stuff
var options = {
			valueNames: ['sort-1', 'sort-2', 'sort-3', 'sort-4', 'sort-5', 'sort-6', 'sort-7', 'sort-8', 'sort-9', 'sort-10', 'sort-11', 'sort-12']
	}
  , documentTable
//, documentTable = new List('mdl-table', options)
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
            getMenuItems();
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
      for(var i=0; i<restaurants.length; i++){
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
        
        span.append(restaurants[i]);
        
        restaurantDiv.appendChild(label);
      }
        var restaurantDiv = document.getElementById('restaurantDiv');
        
        var label = document.createElement('label');
        label.className = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect restaurantCheckboxAll";
        label.setAttribute("for", "checkbox-All");
        
        var input = document.createElement('input');
        label.appendChild(input);
        input.id = "checkbox-All";
        input.className = "mdl-checkbox__input";
        
        var span = document.createElement('span');
        label.appendChild(span);
        span.className = "mdl-checkbox__label";
        
        span.append("Check all");
        
        restaurantDiv.appendChild(label);
        
        label.addEventListener('click', function(e){
          if(!label.classList.contains("is-checked")){
            label.MaterialCheckbox.check();
            $('.mdl-js-checkbox.restaurantCheckbox').each(function (index, element) {
              element.MaterialCheckbox.check();
              e.stopPropagation();
              e.preventDefault();
            });
          }else{
            label.MaterialCheckbox.uncheck();
            $('.mdl-js-checkbox.restaurantCheckbox').each(function (index, element) {
              element.MaterialCheckbox.uncheck();
              e.stopPropagation();
              e.preventDefault();
            });
          }
          e.stopPropagation();
          e.preventDefault();
        });
            
      componentHandler.upgradeAllRegistered();
      
      $('.mdl-js-checkbox.restaurantCheckbox').each(function (index, element) {
        element.addEventListener('click', function(e){
          if(!element.classList.contains("is-checked")){
            element.MaterialCheckbox.check();
          }else{
            element.MaterialCheckbox.uncheck();
          }
          e.stopPropagation();
          e.preventDefault();
        });
      })
    })
    .fail(function(){
      var restaurantDiv = document.getElementById('restaurantDiv');
      restaurantDiv.append("Failed to load. Try refreshing");
      console.log("Failed to load");
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

      
    var dialog2 = document.getElementById("dialog2__content");
    dialog2.appendChild(document.createElement('br'));
    dialog2.appendChild(document.createElement('br'));
    dialog2.append(lastModified);
    })
      .fail(function(){
        alert("Failed to load");
    });
    
    dialog.querySelector('#buttonDatabase')
        .addEventListener('click', function() {
            updateDatabase();
            dialog.close();
    });
    dialog.querySelector('button:not([disabled])')
        .addEventListener('click', function() {
            dialog.close();
        });
}());

//database stuff
function getMenuItems(){
      var label2 = $("#tableHeaderLabel").find(".mdl-checkbox__box-outline");
          label2.hide();
          label2 = $("#tableHeaderLabel").find(".mdl-checkbox__focus-helper");
          label2.hide();
          label2 = $("#tableHeaderLabel").find(".mdl-checkbox__ripple-container");
          label2.hide();
          label2 = $("#tableHeaderLabel").find(".mdl-checkbox__input");
          label2.hide();
  if(documentTable != null){
    documentTable.clear();
    var label = document.getElementById("tableHeaderLabel");
    var label2 = $("#tableHeaderLabel.mdl-checkbox__box-outline");
    label.removeEventListener('click', handler, false);
    label2.hide();
    var searchField = document.getElementById("mdl-table"),
    searchFieldClone = searchField.cloneNode(true);
    searchField.parentNode.replaceChild(searchFieldClone, searchField);

  }
  var menuItemJSON;
  
  var restaurants = [];
  //loop through all checked elements, get restaurant names
  $('.mdl-js-checkbox.restaurantCheckbox').each(function (index, element) {
    if(element.classList.contains("is-checked")){
      restaurants.push(element.getElementsByClassName("mdl-checkbox__label")[0].innerHTML);
    }
  });
  
  var menuItems = [];
  $.ajax({
    url: url,
    type: "POST",
    dataType: "json",
    data: '{"operation":"get", "restaurants":' + JSON.stringify(restaurants) + '}'
  })
  .done(function(data){
    for(var i=0;i<data.menuData.length;i++){
      var array = new Array();
      array.push(data.menuData[i].name);
      array.push("$" + data.menuData[i].cost.toFixed(2));
      array.push(data.menuData[i].restaurant);
      array.push(data.menuData[i].calories);
      array.push(data.menuData[i].fat);
      array.push(data.menuData[i].cholesterol);
      array.push(data.menuData[i].sodium);
      array.push(data.menuData[i].carbohydrates);
      array.push(data.menuData[i].fiber);
      array.push(data.menuData[i].sugars);
      array.push(data.menuData[i].protein);
      array.push(data.menuData[i].allergens);
      menuItems.push(array);
    }
	var table = document.getElementById("menuItemTable");
	//clear table
	while (table.firstChild) {
		table.removeChild(table.firstChild);
	}
  //reset cumulative nutrition
  updateNutrition();
	//populate table
	var $table = $('#menuItemTable');
  
  var j = 0;
	menuItems.forEach(function(element) {
    
    var tr = document.createElement('tr');
    
    //add checkbox
    var buttonTd = document.createElement('td');
    
    var buttonLabel = document.createElement('label');
    buttonLabel.className = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect mdl-data-table__select tableButton"
    buttonLabel.setAttribute("for", "row[" + j +"]");
    
    var buttonInput = document.createElement('input');
    buttonInput.type = "checkbox";
    buttonInput.id = "row[" + j + "]";
    buttonInput.className = "mdl-checkbox__input";
    
    buttonLabel.appendChild(buttonInput);
    
    buttonTd.appendChild(buttonLabel);
    
    tr.appendChild(buttonTd);
    
    //add table data
    var name = document.createElement('td');
		name.className = "mdl-data-table__cell--non-numeric sort-1";
    
    var nameLength = menuItems[j][0].length;
    if(nameLength > 30){
      name.style = "font-size: 10px";
    }else
    if(nameLength > 20){
      name.style = "font-size: 12px";
    }
    else{
      name.style = "font-size: 14px";
    }
    name.append(menuItems[j][0]);
		tr.appendChild(name);
    
    for(var k=1; k<=10; k++){
      var elem = document.createElement('td');
      elem.className = "sort-" + (k+1);
      elem.append(menuItems[j][k]);
      tr.appendChild(elem);
    }
    
    var elem = document.createElement('td');
    elem.className = "sort-12";
    for(var i=0; i<menuItems[j][11].length; i++){
      var p = document.createElement('p');
      p.className = "allergens";
      p.append(menuItems[j][11][i]);
      elem.appendChild(p);
    }
    
    tr.appendChild(elem);
    
    table.appendChild(tr);
		j++;
	});
	componentHandler.upgradeAllRegistered();
  
  var label = document.getElementById("tableHeaderLabel");
  mdlComp = new MaterialCheckbox(label);
  label.removeEventListener('click', handler);
  //add listener for header button
  handler = function(e) {
    console.log("hi");
      if(!label.classList.contains("is-checked")){
        mdlComp.check();
        $('.mdl-js-checkbox.tableButton').each(function (index, element) {
          element.MaterialCheckbox.check();
          e.stopPropagation();
          e.preventDefault();
        });
      }else{
        mdlComp.uncheck();
        $('.mdl-js-checkbox.tableButton').each(function (index, element) {
          element.MaterialCheckbox.uncheck();
          e.stopPropagation();
          e.preventDefault();
        });
      }
  };
  
  label.addEventListener('click', handler, false);

  //update list,js for sort/search

  documentTable = new List('mdl-table-div', options);
    
  })
  .fail(function(){
    alert("Failed to load");
  });
  
}

function updateNutrition(){
}

function updateDatabase(){
  $.ajax({
      url: url,
      type: "POST",
      dataType: "json",
      data: '{"operation":"update"}'
    })
    .done(function(data){
      var updating = data["updating"];
      var snackbarContainer = document.querySelector('#toast');
      
      if(updating == true){
        data = {message: "Already updating"};
      }else{
        data = {message: "Started updating"};
      }
      snackbarContainer.MaterialSnackbar.showSnackbar(data);
    })
    .fail(function(){
      var snackbarContainer = document.querySelector('#toast');

      var data = {message: "Error"};

      snackbarContainer.MaterialSnackbar.showSnackbar(data);
    });
}