/*
 * Copyright 2018-2021 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.package com.theyawns.controller.launcher;
 *
 */

package com.hazelcast.msfdemo.catalogsvc.events;

import com.hazelcast.msf.eventstore.SequencedEvent;
import com.hazelcast.msfdemo.catalogsvc.domain.Item;

import java.io.Serializable;

public abstract class CatalogEvent extends SequencedEvent<Item> implements Serializable {

    protected String itemNumber;

}
