
// Generated file. Do not edit by hand.

package com.microsoft.msnsearch;

public class SourceRequest  implements java.io.Serializable {
    private com.microsoft.msnsearch.SourceType source;

    private int offset;

    private int count;

    private java.lang.String[] resultFields;

    public SourceRequest() {
    }

    public SourceRequest(
           com.microsoft.msnsearch.SourceType source,
           int offset,
           int count,
           java.lang.String[] resultFields) {
           this.source = source;
           this.offset = offset;
           this.count = count;
           this.resultFields = resultFields;
    }


    /**
     * Gets the source value for this SourceRequest.
     * 
     * @return source
     */
    public com.microsoft.msnsearch.SourceType getSource() {
        return source;
    }


    /**
     * Sets the source value for this SourceRequest.
     * 
     * @param source
     */
    public void setSource(com.microsoft.msnsearch.SourceType source) {
        this.source = source;
    }


    /**
     * Gets the offset value for this SourceRequest.
     * 
     * @return offset
     */
    public int getOffset() {
        return offset;
    }


    /**
     * Sets the offset value for this SourceRequest.
     * 
     * @param offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }


    /**
     * Gets the count value for this SourceRequest.
     * 
     * @return count
     */
    public int getCount() {
        return count;
    }


    /**
     * Sets the count value for this SourceRequest.
     * 
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }


    /**
     * Gets the resultFields value for this SourceRequest.
     * 
     * @return resultFields
     */
    public java.lang.String[] getResultFields() {
        return resultFields;
    }


    /**
     * Sets the resultFields value for this SourceRequest.
     * 
     * @param resultFields
     */
    public void setResultFields(java.lang.String[] resultFields) {
        this.resultFields = resultFields;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SourceRequest)) return false;
        SourceRequest other = (SourceRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            this.offset == other.getOffset() &&
            this.count == other.getCount() &&
            ((this.resultFields==null && other.getResultFields()==null) || 
             (this.resultFields!=null &&
              java.util.Arrays.equals(this.resultFields, other.getResultFields())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        _hashCode += getOffset();
        _hashCode += getCount();
        if (getResultFields() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResultFields());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResultFields(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SourceRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "SourceType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offset");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Offset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("count");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "Count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultFields");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ResultFields"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/MSNSearch/2005/09/fex", "ResultFieldMask"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
