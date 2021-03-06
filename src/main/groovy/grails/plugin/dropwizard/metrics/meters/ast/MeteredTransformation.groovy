/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.dropwizard.metrics.meters.ast

import grails.plugin.dropwizard.metrics.NamedMetricTransformation
import grails.plugin.dropwizard.metrics.meters.Meterable
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation
@CompileStatic
class MeteredTransformation extends NamedMetricTransformation {

    @Override
    protected void doTransformation(final AnnotationNode annotationNode,
                                    final MethodNode methodNode,
                                    final SourceUnit source,
                                    final Expression nameExpression) {
        implementTrait(methodNode.declaringClass, Meterable, source)
        final ArgumentListExpression markMeterArgs = new ArgumentListExpression()
        markMeterArgs.addExpression(nameExpression)
        final Expression markExpression = new MethodCallExpression(new VariableExpression('this'), 'markMeter', markMeterArgs)
        final BlockStatement newCode = new BlockStatement()

        newCode.addStatement(new ExpressionStatement(markExpression))

        final Statement originalMethodCode = methodNode.code
        newCode.addStatement(originalMethodCode)

        methodNode.code = newCode
    }
}
