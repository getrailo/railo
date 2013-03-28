package railo.transformer.bytecode.statement.tag;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ConditionVisitor;
import railo.transformer.bytecode.visitor.DecisionIntVisitor;

public final class TagSwitch extends TagBaseNoFinal {

	// int listFindNoCase(String list, String value, String delimiter)
	private static final Method LIST_FIND_NO_CASE = new Method(
														"listFindForSwitch",
														Types.INT_VALUE,
														new Type[]{Types.STRING,Types.STRING,Types.STRING});

	/**
	 * Constructor of the class
	 * @param sl
	 * @param el
	 */
	public TagSwitch(Position start,Position end) {
		super(start,end);
	}
	
	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();

		// expression
		int expression=adapter.newLocal(Types.STRING);
		getAttribute("expression").getValue().writeOut(bc, Expression.MODE_REF);
		adapter.storeLocal(expression);
		
		
		List statements = getBody().getStatements();
		Statement stat;
		Tag tag;

		ConditionVisitor cv=new ConditionVisitor();
		cv.visitBefore();

		// cases
		Iterator it = statements.iterator();
		Tag def=null;
		while(it.hasNext()) {
			stat=(Statement) it.next();
			if(stat instanceof Tag) {
				tag=(Tag) stat;
				if(tag.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Case"))	{
					addCase(bc,cv,tag,expression);
					continue;
				}
				else if(tag.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Defaultcase"))	{
					if(def!=null)
						throw new BytecodeException("multiple defaultcases are not allowed",getStart());
					def=tag;
					//setDefaultCase(bc,cv,tag);
					//break;
				}
			}
		}

		// default
		if(def!=null)setDefaultCase(bc,cv,def);
		
		cv.visitAfter(bc);
		
		
		/*
		 
<!-- cases -->
	<xsl:for-each select="./body/tag[@name='case']">
		if(List.listFindNoCase(case.value,expression,
			<xsl:if test="./attribute[@name='delimiters']">,delimiters)!=-1) {
		<xsl:apply-templates select="./body/*"/>
		}
	</xsl:for-each>
	
<!-- default -->
	<xsl:if test="./body/tag[@name='defaultcase']">
		<xsl:if test="count(./body/tag[@name='case'])&gt;0">else </xsl:if> {
			<xsl:apply-templates select="./body/tag[@name='defaultcase']/body/*"/>
		}
	</xsl:if>
</xsl:template>*/
	}

	private void setDefaultCase(BytecodeContext bc, ConditionVisitor cv, Tag tag) throws BytecodeException {
		cv.visitOtherviseBeforeBody();
			tag.getBody().writeOut(bc);
		cv.visitOtherviseAfterBody();
	}

	private void addCase(BytecodeContext bc, ConditionVisitor cv, Tag tag, int expression) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		
		cv.visitWhenBeforeExpr();
			DecisionIntVisitor div=new DecisionIntVisitor();
			div.visitBegin();
				// List.listFindNoCase(case.value,expression,del);
				tag.getAttribute("value").getValue().writeOut(bc,Expression.MODE_REF);
				adapter.loadLocal(expression);
				Attribute attr = tag.getAttribute("delimiters");
				if(attr!=null)attr.getValue().writeOut(bc,Expression.MODE_REF);
				else adapter.push(",");
				adapter.invokeStatic(Types.LIST_UTIL, LIST_FIND_NO_CASE);
			div.visitNEQ();
				adapter.push(-1);
			div.visitEnd(bc);
		cv.visitWhenAfterExprBeforeBody(bc);
			tag.getBody().writeOut(bc);
		cv.visitWhenAfterBody(bc);
		
		
		
		/*if(List.listFindNoCase(case.value,expression,delimiters)!=-1) {
		<xsl:apply-templates select="./body/*"/>
		}*/
	}

}
