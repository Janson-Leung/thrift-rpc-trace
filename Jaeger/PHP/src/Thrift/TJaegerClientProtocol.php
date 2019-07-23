<?php

namespace Asan\Thrift;

use Asan\Trace\JaegerTracer;
use Jaeger\Config;
use Thrift\Exception\TApplicationException;
use Thrift\Protocol\TBinaryProtocol;
use Thrift\Type\TType;
use Thrift\Type\TMessageType;
use OpenTracing\Formats;

/**
 *
 *
 * @author Janson
 * @create 2019-06-18
 */

class TJaegerClientProtocol extends TBinaryProtocol {

    private $serverName;

    public function __construct($serverName, $transport, $strictRead = false, $strictWrite = true) {
        parent::__construct($transport, $strictRead, $strictWrite);

        $this->serverName = $serverName;
    }

    public function readMessageBegin(&$name, &$type, &$seqId) {
        $result = parent::readMessageBegin($name, $type, $seqId);

        if ($type == TMessageType::EXCEPTION) {
            $x = new TApplicationException();
            $x->read($this);
            //$this->readMessageEnd();

            $activeSpan = JaegerTracer::getTracer($this->serverName)->activeSpan;

            $activeSpan->log(['Exception' => $name . ': ' . $x->getMessage()]);
            $activeSpan->finish();

            Config::getInstance()->flush();
        } elseif ($type == TMessageType::REPLY) {
            $activeSpan = JaegerTracer::getTracer($this->serverName)->activeSpan;
            $activeSpan->finish();

            Config::getInstance()->flush();
        }

        return $result;
    }

    public function writeMessageBegin($name, $type, $seqId) {
        parent::writeMessageBegin($name, $type, $seqId);

        // write trace info to field0
        $this->writeFieldZero($name);
    }

    public function writeFieldZero($name) {
        $this->writeFieldBegin('traceContext', TType::STRING, 0);

        $tracer = JaegerTracer::getTracer($this->serverName);

        $contextCarrier = [];
        //$spanContext = $tracer->extract(Formats\TEXT_MAP, $carrier);
        $tracer->activeSpan = $tracer->startSpan("PHP site: rpc.thrift $name start");
        $tracer->inject($tracer->activeSpan->getContext(), Formats\TEXT_MAP, $contextCarrier);

        $this->writeString(json_encode(['traceContext' => $contextCarrier]));

        $this->writeFieldEnd();
    }
}
