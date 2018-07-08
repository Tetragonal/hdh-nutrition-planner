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
Create a table called "menu_items" with the following attributes:
rainbowcat-jetty::DATABASE=> select column_name, data_type from information_schema.columns where table_name = 'menu_items';
  column_name  |          data_type
---------------+-----------------------------
 name          | character varying
 restaurant    | character varying
 cost          | money
 calories      | integer
 fat           | double precision
 saturated_fat | double precision
 trans_fat     | double precision
 cholesterol   | double precision
 sodium        | double precision
 carbohydrates | double precision
 fiber         | double precision
 sugars        | double precision
 protein       | double precision
 allergens     | ARRAY
 monday        | boolean
 tuesday       | boolean
 wednesday     | boolean
 thursday      | boolean
 friday        | boolean
 saturday      | boolean
 sunday        | boolean
 added         | timestamp without time zone
(22 rows)