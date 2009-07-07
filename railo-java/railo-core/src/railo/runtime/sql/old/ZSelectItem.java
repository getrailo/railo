package railo.runtime.sql.old;


// Referenced classes of package Zql:
//            ZAliasedName, ZConstant, ZUtils, ZExp

public final class ZSelectItem extends ZAliasedName
{

    public ZSelectItem()
    {
        expression_ = null;
        aggregate_ = null;
    }

    public ZSelectItem(String s)
    {
        super(s, ZAliasedName.FORM_COLUMN);
        expression_ = null;
        aggregate_ = null;
        setAggregate(ZUtils.getAggregateCall(s));
    }

    public ZExp getExpression()
    {
        if(isExpression())
            return expression_;
        if(isWildcard())
            return null;
        return new ZConstant(getColumn(), 0);
    }

    public void setExpression(ZExp zexp)
    {
        expression_ = zexp;
        strform_ = expression_.toString();
    }

    public boolean isExpression()
    {
        return expression_ != null;
    }

    public void setAggregate(String s)
    {
        aggregate_ = s;
    }

    public String getAggregate()
    {
        return aggregate_;
    }

    ZExp expression_;
    String aggregate_;
}