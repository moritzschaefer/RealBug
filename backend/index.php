<?php

include_once './Slim/Slim.php';
include_once 'API.php';

\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();
$app->get('/RealBug/:id', function($id){
		$api = new RealBugAPI();
		$api->getRealBug($id);
	});
	
$app->get('/RealBug', function(){
		$api = new RealBugAPI();
		$api->getRealBugs();
	});
	
$app->post('/RealBug', function(){
		$api = new RealBugAPI();
		$api->addBug();
	});

		
$app->run();

?>
