# hdh-nutrition-planner
This is a utility for viewing UCSD dining hall nutrition information. 

## Usage: 
To use the tool, click "Load Restaurants" and select the restaurants to view. 
Then, mark some checkboxes and click "Nutrition Information" to view the cumulative nutrition information for the selected items.

## Details:
The site runs on a jQuery frontend and a Jetty (Java) backend, hosted on Heroku with the PostgreSQL plugin.
The information is scraped from https://hdh.ucsd.edu/DiningMenus/ 
