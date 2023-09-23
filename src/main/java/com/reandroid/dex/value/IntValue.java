/*
 *  Copyright (C) 2022 github.com/REAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reandroid.dex.value;

import com.reandroid.arsc.item.IntegerReference;
import com.reandroid.arsc.item.IntegerVisitor;
import com.reandroid.arsc.item.VisitableInteger;

public class IntValue extends PrimitiveValue implements IntegerReference, VisitableInteger {

    public IntValue() {
        super();
    }

    @Override
    public void visitIntegers(IntegerVisitor visitor) {
        visitor.visit(this, this);
    }

    @Override
    public int get() {
        return getAsInteger(0);
    }
    @Override
    public void set(int value) {
        setNumberValue(value);
    }
}
