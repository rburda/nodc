This application's purpose is to pull hotel results from www.neworleans.com and 
www.frenchquarterguide.com and present them in a unified manner. Booking 
is accomplished by linking to the underlying websites.

The code is located in a git repository: git@github.com:howardlef/NODC.git 

The code is written using the following technologies:

Java (language)
Maven (build tool and dependency management)
Spring (java libraries for MVC framework, AOP, amongst others)
JSoup (java library for screenscraping -- for pulling results from neworleans.com/frenchquarterguide.com)
Freemarker (html templates / rendering)
Bootstrap (css framework for admin site)
DynamoDB (database)
AWS (cloud deployment, reliability, scalability, etc...)

Application Architecture
The app is organized into the following structure:
com
  nodc
    scraper
    	cache
    		 * -- Scheduled tasks that are run behind the scenes to refresh static content
    		 			related to hotels (descriptions, images, amenities, etc...). Separate
    		 			code for neworleans.com and frenchquarterguide.com
      controller
         SearchController -- hotel searching / results
         AdminController -- admin application 
         CacheAdminController -- manual content cache refresh        
      inventory
         InventoryServiceImpl -- main entry into search functionality
         AdminServiceImpl -- main entry into admin functionality
         NODCWarehouse -- logic to query underlying www.neworleans.com website
         FrenchQuarterGuideInventorySource -- queries www.frenchquarterguide.com         
      dao
        *DAO  -- logic to communicate with DynamoDB to persist/retrieve hotel 
                 content, external hotel ids neccessary for mapping search results,
                 hotel weights, etc.... 
			model
				*.* -- model objects that are used to represent hotel results. These are
				       used by Freemarker to render the final html pages seen on the main
				       search results as well as the admin screens

There are three maven profiles setup
 dev: 						default profile; when running on localbox this is what is used.
 									Only pulls freemarker templates embedded within app
 live: 						only difference from dev is that the freemarker templates are
 									pulled first from s3. If not found, then ones interally deployed
 									with app are used.
 scheduledTasks:	enables the scheduledTasks (run once a day) that refresh static
 									content from underlying websites and caches in local dynamodb

Deployment/Build

	Local: 
		Must have java, and maven preinstalled
		from root of install directory run the following:
			mvn clean compile jetty:run -Dorg.eclipse.jetty.server.Request.maxFormKeys=8000 -DAWS_ACCESS_KEY_ID=<KEY_ID> -DAWS_SECRET_KEY=<SECRET_KEY>
		NOTE: You must know the aws_access_key and aws_secret_key to run the app even in dev mode. 
		The app points to the LIVE DB even in dev mode so be careful!!!!!
	Live:
	  Build application locally: 
	  	mvn clean compile install -DskipTests
	  Login to AWS using previously supplied credentials. Upload a new version of the app (the one you just built) and redeploy.
	  NOTE: You do not need to redeploy the backend and the live servers at the same time.
	  

AWS resources
   A detailed list of what resources are used in the AWS ecosystem is documented
   in the file 'setup_instructions.txt' (also at root of this project).
   NOTE: Using AWS resources can get expensive quickly. It is advisable to familiarize 
   yourself with the costs associated with the different services prior to using them.
   
Viewing logs
   There are many ways to setup logging in the world of AWS but right now you must 
   login to the ec2 instance(s) that are running the application. 
   This process is also documented in the 'setup_instructions.txt' (step #6 -- setup keypair). 
   The .pem file that is required to login has already been created to the 
   existing ec2 instances has already been created and is being  supplied to you
   separately.