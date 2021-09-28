/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.graalvm.buildtools.gradle

import org.graalvm.buildtools.gradle.fixtures.AbstractFunctionalTest

class DeprecatedExtensionFunctionalTest extends AbstractFunctionalTest {
    def "using a deprecated extension issues a warning"() {
        given:
        buildFile << """
            plugins {
                id 'application'
                id 'org.graalvm.buildtools.native'
            }
            
            $extensionName {
                verbose = true
                jvmArgs('hello', 'world')
            }
            
            assert graalvmNative.binaries.${replacedWith}.verbose.get() == true
            assert graalvmNative.binaries.${replacedWith}.jvmArgs.get() == ['hello', 'world']
        """

        when:
        run 'help'

        then:
        outputContains "The $extensionName extension is deprecated and will be removed. Please use the 'graalvmNative.binaries.$replacedWith' extension to configure the native image instead."

        where:
        extensionName | replacedWith
        'nativeBuild' | 'main'
        'nativeTest'  | 'test'
    }

    def "calling the deprecated nativeBuild task triggers a warning and execution of the native image task"() {
        gradleVersion = version

        given:
        withSample("java-application")

        when:
        run 'nativeBuild'

        then:
        tasks {
            succeeded ':nativeCompile'
            succeeded ':nativeBuild'
        }

        and:
        outputContains 'Task nativeBuild is deprecated. Use nativeCompile instead.'

        where:
        version << TESTED_GRADLE_VERSIONS
    }
}