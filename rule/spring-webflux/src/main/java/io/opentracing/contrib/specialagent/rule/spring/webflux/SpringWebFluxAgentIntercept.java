/* Copyright 2019 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opentracing.contrib.specialagent.rule.spring.webflux;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.server.ServerWebExchange;

import io.opentracing.contrib.specialagent.rule.spring.webflux.copied.TracingExchangeFilterFunction;
import io.opentracing.contrib.specialagent.rule.spring.webflux.copied.TracingOperator;
import io.opentracing.contrib.specialagent.rule.spring.webflux.copied.WebClientSpanDecorator;
import io.opentracing.contrib.specialagent.rule.spring.webflux.copied.WebFluxSpanDecorator;
import io.opentracing.util.GlobalTracer;
import reactor.core.publisher.Mono;

public class SpringWebFluxAgentIntercept {
  public static void client(final Object thiz) {
    final WebClient.Builder builder = (Builder)thiz;
    builder.filter(new TracingExchangeFilterFunction(GlobalTracer.get(), Collections.singletonList(new WebClientSpanDecorator.StandardTags())));
  }

  @SuppressWarnings("unchecked")
  public static Object handle(Object exchange, Object returned) {
    final ServerWebExchange serverWebExchange = (ServerWebExchange)exchange;
    final Mono<Void> res = (Mono<Void>)returned;
    return new TracingOperator(res, serverWebExchange, GlobalTracer.get(), Arrays.asList(new WebFluxSpanDecorator.StandardTags(), new WebFluxSpanDecorator.WebFluxTags()));
  }
}