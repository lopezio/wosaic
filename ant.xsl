<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
    <html>
    <head>
        <style>
        .target {
            background: #ccffff;
            margin-left: 2em;
        }
        </style>
    </head>
    <body>

    <xsl:call-template name="project" />
    <xsl:call-template name="targets" />

    </body>
    </html>
</xsl:template>

<xsl:template name="project">
    <h1>
    <xsl:value-of select="/project/@name"/> Ant Information
    </h1>
</xsl:template>

<xsl:template name="targets">
    <h2>Targets</h2>

    <p>Listed below are the targets defined in build.xml.  Each of these can be
    invoked by calling 'ant [target-name]' where [target-name] is one of the targets
    documented below:</p>
    
    <xsl:for-each select="/project/target">
        <div class="target">
        <h3><xsl:value-of select="@name" /></h3>
        Depends On: <xsl:value-of select="@depends" /> <br/>

        <p><xsl:value-of select="@description" /></p>
        </div>
    </xsl:for-each>

</xsl:template>

</xsl:stylesheet>
