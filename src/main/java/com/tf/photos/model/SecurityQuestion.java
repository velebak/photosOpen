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
package com.tf.photos.model;

/**
 * @author Heather Stevens on 5/11/2014.
 *
 * http://goodsecurityquestions.com/examples.htm
 */
public enum SecurityQuestion
{
	NICKNAME("nickname"),
	SPOUSE_CITY("spouseCity"),
	CHILDHOOD_FRIEND("childhoodFriend"),
	THIRD_GRADE_STREET("thirdGradeStreet"),
	OLDEST_SIBLING_BIRTH("oldestSiblingBirth"),
	OLDEST_CHILD_MIDDLE("oldestChildMiddle"),
	OLDEST_SIBLING_MIDDLE("oldestSiblingMiddle"),
	SIXTH_GRADE_SCHOOL("sixthGradeSchool"),
	CHILDHOOD_PHONE("childhoodPhone"),
	OLDEST_COUSIN_NAME("oldestCousinName"),
	FIRST_STUFFED_ANIMAL("firstStuffedAnimal"),
	PARENTS_MEET_CITY("parentsMeetCity"),
	FIRST_KISS_LOCATION("firstKissLocation"),
	FIRST_NAME_FIRST_KISS("firstNameFirstKiss"),
	THIRD_GRADE_TEACHER("thirdGradeTeacher"),
	CITY_NEAREST_SIBLING("cityNearestSibling"),
	OLDEST_BROTHER_BIRTH("oldestBrotherBirth"),
	MATERNAL_GRAND_MOTHER_MAIDEN("maternalGrandmotherMaiden"),
	CITY_FIRST_JOB("cityFirstJob"),
	WEDDING_RECEPTION_LOCATION("weddingReceptionLocation"),
	COLLEGE_APPLIED_NOT_ATTENDED("collegeAppliedNotAttended"),
	WHERE_911("where911");

	private String label;

	SecurityQuestion(String label) {
		this.label = label;
	}
	public String getLabel() {
		return this.label;
	}

}
