#!/bin/bash

redis-cli XGROUP CREATE events-stream events-group $ MKSTREAM
redis-cli XINFO CONSUMERS events-stream events-group