<?php

use Asan\Thrift\GenPHP\ApiDemo\ApiDemoClient;
use Asan\Thrift\TJaegerClientProtocol;
use Thrift\Transport\TBufferedTransport;
use Thrift\Transport\TSocket;

require '../vendor/autoload.php';

$socket = new TSocket("0.0.0.0", 8600);
$transport = new TBufferedTransport($socket);
//$protocol = new TBinaryProtocol($transport);
//$protocol = new TClientProtocol($transport);
$protocol = new TJaegerClientProtocol("api-demo", $transport);

$transport->open();

$client = new ApiDemoClient($protocol);
$response = $client->getDetailById(2);

$transport->close();

var_dump($response);

