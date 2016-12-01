/*
 *
 * Copyright 2013-2016 Pacific Coast Professional Services, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.tf.photos.backing.filter;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.tf.photos.model.KnownBad;

/**
 * Support filtering of data tables.
 *
 * @author Heather Stevens
 *         Date 7/12/2014
 */
@Named
@SessionScoped
public class KnownBadListFilter extends DataListFilter<KnownBad> implements Serializable {
	private static final long serialVersionUID = -6412553464916429013L;

	public KnownBadListFilter() {
	}

}


