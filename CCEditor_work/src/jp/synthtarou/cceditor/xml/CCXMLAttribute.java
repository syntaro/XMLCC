package jp.synthtarou.cceditor.xml;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCXMLAttribute {
    public CCXMLAttribute(String name) {
        _name = name;
    }

    public CCXMLAttribute(String name, String value) {
        _name = name;
        _value = value;
    }

    private final String _name;
    private String _value;

    public String getName() {
        return _name;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String _value) {
        this._value = _value;
    }
}
