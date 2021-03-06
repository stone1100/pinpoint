/*
 * Copyright 2014 NAVER Corp.
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

package com.navercorp.pinpoint.web.applicationmap.rawdata;

import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.applicationmap.histogram.TimeHistogram;
import com.navercorp.pinpoint.web.vo.Application;
import com.navercorp.pinpoint.web.vo.LinkKey;

import java.util.*;

/**
 * representation of caller/callee relationship 
 * @author emeroad
 */
public class LinkCallData {

    private final String source;
    private final ServiceType sourceServiceType;

    private final String target;
    private final ServiceType targetServiceType;

    private final Map<Long, TimeHistogram> targetHistogramTimeMap;

    public LinkCallData(LinkKey linkKey) {
        if (linkKey == null) {
            throw new NullPointerException("linkKey must not be null");
        }
        this.source = linkKey.getFromApplication();
        this.sourceServiceType = linkKey.getFromServiceType();

        this.target = linkKey.getToApplication();
        this.targetServiceType = linkKey.getToServiceType();

        this.targetHistogramTimeMap = new HashMap<Long, TimeHistogram>();
    }

    public LinkCallData(Application source, Application target) {
        if (source == null) {
            throw new NullPointerException("linkKey must not be null");
        }
        this.source = source.getName();
        this.sourceServiceType = source.getServiceType();

        this.target = target.getName();
        this.targetServiceType = target.getServiceType();

        this.targetHistogramTimeMap = new HashMap<Long, TimeHistogram>();
    }



    public String getSource() {
        return source;
    }

    public ServiceType getSourceServiceType() {
        return sourceServiceType;
    }

    public String getTarget() {
        return target;
    }

    public ServiceType getTargetServiceType() {
        return targetServiceType;
    }

    public Collection<TimeHistogram> getTimeHistogram() {
        return targetHistogramTimeMap.values();
    }

    public void addCallData(long timestamp, short slot, long count) {
        TimeHistogram histogram = getTimeHistogram(timestamp);
        histogram.addCallCount(slot, count);
    }

    public void addCallData(Collection<TimeHistogram> timeHistogramList) {
        for (TimeHistogram timeHistogram : timeHistogramList) {
            TimeHistogram histogram = getTimeHistogram(timeHistogram.getTimeStamp());
            histogram.add(timeHistogram);
        }
    }

    public void addRawCallData(LinkCallData copyLinkCallData) {
        if (copyLinkCallData == null) {
            throw new NullPointerException("copyLinkCallData must not be null");
        }
        if (!this.source.equals(copyLinkCallData.source)) {
            throw new IllegalArgumentException("source not equals");
        }
        if (this.sourceServiceType != copyLinkCallData.sourceServiceType) {
            throw new IllegalArgumentException("sourceServiceType not equals");
        }
        if (!this.target.equals(copyLinkCallData.target)) {
            throw new IllegalArgumentException("target not equals");
        }
        if (this.targetServiceType != copyLinkCallData.targetServiceType) {
            throw new IllegalArgumentException("targetServiceType not equals");
        }

        for (Map.Entry<Long, TimeHistogram> copyEntry : copyLinkCallData.targetHistogramTimeMap.entrySet()) {
            final Long timeStamp = copyEntry.getKey();
            TimeHistogram histogram = getTimeHistogram(timeStamp);
            histogram.add(copyEntry.getValue());
        }
    }

    private TimeHistogram getTimeHistogram(Long timeStamp) {
        TimeHistogram histogram = targetHistogramTimeMap.get(timeStamp);
        if (histogram == null) {
            histogram = new TimeHistogram(targetServiceType, timeStamp);
            targetHistogramTimeMap.put(timeStamp, histogram);
        }
        return histogram;
    }

    public long getTotalCount() {
        long totalCount = 0;
        for (TimeHistogram timeHistogram : targetHistogramTimeMap.values()) {
            totalCount += timeHistogram.getTotalCount();
        }
        return totalCount;
    }

    @Override
    public String toString() {
        return "LinkCallData{" +
                "source='" + source + '\'' +
                ", sourceServiceType=" + sourceServiceType +
                ", target='" + target + '\'' +
                ", targetServiceType=" + targetServiceType +
                '}';
    }
}
