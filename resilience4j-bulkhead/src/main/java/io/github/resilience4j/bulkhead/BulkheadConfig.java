/*
 *
 *  Copyright 2016 Robert Winkler, Lucas Lech
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package io.github.resilience4j.bulkhead;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.time.Duration;

/**
 * A {@link BulkheadConfig} configures a {@link Bulkhead}
 */
@Immutable
public class BulkheadConfig implements Serializable {

    private static final long serialVersionUID = -9139631465007403460L;

    public static final int DEFAULT_MAX_CONCURRENT_CALLS = 25;
    public static final Duration DEFAULT_MAX_WAIT_DURATION = Duration.ofSeconds(0);
    public static final boolean DEFAULT_WRITABLE_STACK_TRACE_ENABLED = true;
    public static final boolean DEFAULT_FAIR_CALL_HANDLING_STRATEGY_ENABLED = true;

    private final int maxConcurrentCalls;
    private final Duration maxWaitDuration;
    private final boolean writableStackTraceEnabled;
    private final boolean fairCallHandlingEnabled;

    private BulkheadConfig(int maxConcurrentCalls, Duration maxWaitDuration,
        boolean writableStackTraceEnabled, boolean fairCallHandlingEnabled) {
        this.maxConcurrentCalls = maxConcurrentCalls;
        this.maxWaitDuration = maxWaitDuration;
        this.writableStackTraceEnabled = writableStackTraceEnabled;
        this.fairCallHandlingEnabled = fairCallHandlingEnabled;
    }

    /**
     * Returns a builder to create a custom BulkheadConfig.
     *
     * @return a {@link Builder}
     */
    public static Builder custom() {
        return new Builder();
    }

    /**
     * Returns a builder to create a custom BulkheadConfig.
     *
     * @return a {@link Builder}
     */
    public static Builder from(BulkheadConfig baseConfig) {
        return new Builder(baseConfig);
    }

    /**
     * Creates a default Bulkhead configuration.
     *
     * @return a default Bulkhead configuration.
     */
    public static BulkheadConfig ofDefaults() {
        return new Builder().build();
    }

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }

    public Duration getMaxWaitDuration() {
        return maxWaitDuration;
    }

    public boolean isWritableStackTraceEnabled() {
        return writableStackTraceEnabled;
    }

    public boolean isFairCallHandlingEnabled() {
        return fairCallHandlingEnabled;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BulkheadConfig{");
        sb.append("maxConcurrentCalls=").append(maxConcurrentCalls);
        sb.append(", maxWaitDuration=").append(maxWaitDuration);
        sb.append(", writableStackTraceEnabled=").append(writableStackTraceEnabled);
        sb.append(", fairCallHandlingEnabled=").append(fairCallHandlingEnabled);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {

        private int maxConcurrentCalls;
        private Duration maxWaitDuration;
        private boolean writableStackTraceEnabled;
        private boolean fairCallHandlingEnabled;

        public Builder() {
            this.maxConcurrentCalls = DEFAULT_MAX_CONCURRENT_CALLS;
            this.maxWaitDuration = DEFAULT_MAX_WAIT_DURATION;
            this.writableStackTraceEnabled = DEFAULT_WRITABLE_STACK_TRACE_ENABLED;
            this.fairCallHandlingEnabled = DEFAULT_FAIR_CALL_HANDLING_STRATEGY_ENABLED;
        }

        public Builder(BulkheadConfig bulkheadConfig) {
            this.maxConcurrentCalls = bulkheadConfig.getMaxConcurrentCalls();
            this.maxWaitDuration = bulkheadConfig.getMaxWaitDuration();
            this.writableStackTraceEnabled = bulkheadConfig.isWritableStackTraceEnabled();
            this.fairCallHandlingEnabled = bulkheadConfig.isFairCallHandlingEnabled();
        }

        /**
         * Configures the max amount of concurrent calls the bulkhead will support.
         *
         * @param maxConcurrentCalls max concurrent calls
         * @return the BulkheadConfig.Builder
         */
        public Builder maxConcurrentCalls(int maxConcurrentCalls) {
            if (maxConcurrentCalls < 0) {
                throw new IllegalArgumentException(
                    "maxConcurrentCalls must be an integer value >= 0");
            }
            this.maxConcurrentCalls = maxConcurrentCalls;
            return this;
        }

        /**
         * Configures a maximum amount of time which the calling thread will wait to enter the
         * bulkhead. If bulkhead has space available, entry is guaranteed and immediate. If bulkhead
         * is full, calling threads will contest for space, if it becomes available. maxWaitDuration
         * can be set to 0.
         * <p>
         * Note: for threads running on an event-loop or equivalent (rx computation pool, etc),
         * setting maxWaitDuration to 0 is highly recommended. Blocking an event-loop thread will
         * most likely have a negative effect on application throughput.
         *
         * @param maxWaitDuration maximum wait time for bulkhead entry
         * @return the BulkheadConfig.Builder
         */
        public Builder maxWaitDuration(Duration maxWaitDuration) {
            if (maxWaitDuration.toMillis() < 0) {
                throw new IllegalArgumentException(
                    "maxWaitDuration must be a positive integer value >= 0");
            }
            this.maxWaitDuration = maxWaitDuration;
            return this;
        }

        /**
         * Enables writable stack traces. When set to false, {@link Exception#getStackTrace()}
         * returns a zero length array. This may be used to reduce log spam when the circuit breaker
         * is open as the cause of the exceptions is already known (the circuit breaker is
         * short-circuiting calls).
         *
         * @param writableStackTraceEnabled flag to control if stack trace is writable
         * @return the BulkheadConfig.Builder
         */
        public Builder writableStackTraceEnabled(boolean writableStackTraceEnabled) {
            this.writableStackTraceEnabled = writableStackTraceEnabled;
            return this;
        }

        /**
         * Indicates whether FairSync or NonfairSync should be used in Semaphore.
         * When set to true, a fair call handling strategy is used. It guarantees the order of incoming requests (FIFO)
         * based on internal queue.
         * When set to false, an non fair strategy will be used which does not guarantee any order of calls.
         *
         * @param fairCallHandlingEnabled flag to choose call handling strategy between fair and unfair
         * @return the BulkheadConfig.Builder
         */
        public Builder fairCallHandlingStrategyEnabled(boolean fairCallHandlingEnabled) {
            this.fairCallHandlingEnabled = fairCallHandlingEnabled;
            return this;
        }

        /**
         * Builds a BulkheadConfig
         *
         * @return the BulkheadConfig
         */
        public BulkheadConfig build() {
            return new BulkheadConfig(maxConcurrentCalls, maxWaitDuration,
                writableStackTraceEnabled, fairCallHandlingEnabled);
        }
    }
}
