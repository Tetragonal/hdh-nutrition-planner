# hdh-nutrition-planner
This is a utility for viewing UCSD dining hall nutrition information. 

## Usage: 
To use the tool, click "Load Restaurants" and select the restaurants to view. 
Then, mark some checkboxes and click "Nutrition Information" to view the cumulative nutrition information for the selected items.

## Details:
The site runs on a jQuery frontend and a Jetty (Java) backend, hosted on Heroku with the PostgreSQL plugin.
The information is scraped from https://hdh.ucsd.edu/DiningMenus/ 

## Dependencies:
[Jquery](https://jquery.com/)

[Material Design Lite](https://getmdl.io/started/)

[list.js](http://listjs.com/)

## PSQL setup:
Create a table called "menu_items" with the following attributes:<br />
rainbowcat-jetty::DATABASE=> select column_name, data_type from information_schema.columns where table_name = 'menu_items';<br />
  column_name  |          data_type<br />
---------------+----------------------------- <br />
 name          | character varying<br />
 restaurant    | character varying<br />
 cost          | money<br />
 calories      | integer<br />
 fat           | double precision<br />
 saturated_fat | double precision<br />
 trans_fat     | double precision<br />
 cholesterol   | double precision<br />
 sodium        | double precision<br />
 carbohydrates | double precision<br />
 fiber         | double precision<br />
 sugars        | double precision<br />
 protein       | double precision<br />
 allergens     | ARRAY<br />
 monday        | boolean<br />
 tuesday       | boolean<br />
 wednesday     | boolean<br />
 thursday      | boolean<br />
 friday        | boolean<br />
 saturday      | boolean<br />
 sunday        | boolean<br />
 added         | timestamp without time zone<br />
(22 rows)