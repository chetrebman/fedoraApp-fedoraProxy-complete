<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:mets="http://www.loc.gov/METS/"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:foxml="info:fedora/fedora-system:def/foxml#" xmlns:xlink="http://www.w3.org/1999/xlink"
    version="2.0">

    <xsl:template match="mets:mets">
        <foxml:digitalObject xmlns:foxml="info:fedora/fedora-system:def/foxml#"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="info:fedora/fedora-system:def/foxml# http://fedora-commons.org/definitions/1/0/foxml1-0.xsd">
            <foxml:objectProperties>
                <foxml:property NAME="http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
                    VALUE="FedoraObject"/>
                <foxml:property NAME="info:fedora/fedora-system:def/model#state" VALUE="A"/>
                
                <xsl:element name="foxml:property">
                    <xsl:attribute name="NAME">info:fedora/fedora-system:def/model#label</xsl:attribute>
                    <xsl:attribute name="VALUE">
                        <xsl:value-of select="@LABEL"/>
                    </xsl:attribute>
                </xsl:element>
                
                <!-- EXTENSIBLE OBJECT PROPERTIES -->
                <!-- These are optional, user-defined object properties, for example an OAI identifier for the object. -->
                <!-- Fedora will store these, but it will not recognize them or operate on them. -->
                <foxml:extproperty NAME="penrose.systems.du.edu"
                    VALUE="Batch Ingest v1.0 "/>
            </foxml:objectProperties>
            
            
            <!-- create a datastream for the root (mets) element-->
            <xsl:element name="foxml:datastream">
                <xsl:attribute name="ID">METS_2</xsl:attribute>
                <xsl:attribute name=" STATE">A</xsl:attribute>
                <xsl:attribute name=" CONTROL_GROUP">X</xsl:attribute>
                <xsl:attribute name=" VERSIONABLE">true</xsl:attribute>
                
                <xsl:element name="foxml:datastreamVersion">
                    <xsl:attribute name="ID">METS.0</xsl:attribute>
                    <!--  TBD only allows one of each DS type -->
                    <xsl:attribute name="MIMETYPE">text/xml</xsl:attribute>
                    <xsl:attribute name="LABEL">METS</xsl:attribute>
                    <foxml:xmlContent>
                        
                        <!-- redeclare the namespaces that are already at the top of this file so they are attached to DataStream block -->
                        <xsl:text disable-output-escaping="yes">
                            <![CDATA[ <mets:mets xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xmlns:xlink="http://www.w3.org/1999/xlink"> ]]>
                        </xsl:text>
                        
                        <xsl:copy-of select="../node()"/>
                        
                        <xsl:text disable-output-escaping="yes">
                            <![CDATA[ </mets:mets> ]]></xsl:text>
                        
                    </foxml:xmlContent>
                </xsl:element>
            </xsl:element>
            
            <!-- create datastreams for all objects contained in mets -->
            <!-- <xsl:apply-templates/>  -->
            
            <xsl:for-each select="mets:dmdSec[not(@ID='dmdAlliance')]"> <xsl:apply-templates/></xsl:for-each>
            
             <xsl:for-each select="mets:fileSec"> <xsl:apply-templates/></xsl:for-each>
            
        </foxml:digitalObject>
    </xsl:template>
  
    <xsl:template match="mets:dmdSec/mets:mdWrap">
        <xsl:element name="foxml:datastream">
            <xsl:attribute name="ID">
                <xsl:value-of select="@MDTYPE"/></xsl:attribute>
            <xsl:attribute name=" STATE">A</xsl:attribute>
            <xsl:attribute name=" CONTROL_GROUP">X</xsl:attribute>
            <xsl:attribute name=" VERSIONABLE">true</xsl:attribute>
            <xsl:element name="foxml:datastreamVersion">
                <xsl:attribute name="ID">
                    <xsl:value-of select="@MDTYPE"/><![CDATA[.0]]></xsl:attribute>
                <xsl:attribute name="MIMETYPE">text/xml</xsl:attribute>
                <xsl:attribute name="LABEL">
                    <xsl:value-of select="@MDTYPE"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="mets:rightsMD ">
        <xsl:element name="foxml:datastream">
            <xsl:attribute name="ID">
                <xsl:value-of select="@ID"/></xsl:attribute>
            <xsl:attribute name=" STATE">A</xsl:attribute>
            <xsl:attribute name=" CONTROL_GROUP">X</xsl:attribute>
            <xsl:attribute name=" VERSIONABLE">true</xsl:attribute>
            <xsl:element name="foxml:datastreamVersion">
                <xsl:attribute name="ID">
                    <xsl:value-of select="@ID"/><![CDATA[.0]]></xsl:attribute>
                <xsl:attribute name="MIMETYPE">text/xml</xsl:attribute>
                <xsl:attribute name="LABEL">
                    <xsl:value-of select="@ID"/>
                </xsl:attribute>
                <xsl:apply-templates/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="mets:xmlData">
        <foxml:xmlContent>
            <xsl:copy-of select="./node()"/>
        </foxml:xmlContent>
    </xsl:template>

    <xsl:template match="mets:file">
              
        <xsl:variable name="USEVALUE" select="../@USE" />
        
        <xsl:element name="foxml:datastream">
            <xsl:attribute name="ID">
                <xsl:value-of select="@ID"/>
            </xsl:attribute>
            <xsl:attribute name=" STATE">A</xsl:attribute>
            <xsl:attribute name=" CONTROL_GROUP">M</xsl:attribute>
            <xsl:attribute name=" VERSIONABLE">true</xsl:attribute>  

        
        <xsl:element name="foxml:datastreamVersion">
            <xsl:attribute name="ID"><xsl:value-of select="@ID"/><xsl:value-of select="generate-id()"/></xsl:attribute>
            <!--  TBD only allows one of each DS type -->
            <xsl:attribute name="MIMETYPE">
                <xsl:value-of select="@MIMETYPE"/>
            </xsl:attribute>
            <xsl:attribute name="LABEL">
                <xsl:value-of select="$USEVALUE"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </xsl:element>
        </xsl:element> 
        
    </xsl:template>
    
    <xsl:template match="mets:FLocat">
        <xsl:element name="foxml:contentLocation">
            <xsl:attribute name="TYPE">
                <xsl:value-of select="@LOCTYPE"/>
            </xsl:attribute>
            <xsl:attribute name="REF">
                <xsl:value-of select="@xlink:href"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>   

</xsl:stylesheet>
