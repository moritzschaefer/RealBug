<?php

include_once './slim/Slim.php';
include_once 'API.php';

$server = "ec2-54-243-188-54.compute-1.amazonaws.com";
$port = 5482;
$db = "d8253bs8koqicj";
$user = "rmtcunwfewenbn";
$pw = "utQ0pLnQyZTJopK1UX4QSjCMwf";

$api = new RealBugAPI($server, $port, $db, $user, $pw);

/*$app = new \Slim\Slim();
$app->get('/RealBug/:ID', $api->getRealBug($ID));
$app->run();
$response = $app->response();
$response['Content-Type'] = 'application/json';
*/

$api->getRealBug(1);

?>
