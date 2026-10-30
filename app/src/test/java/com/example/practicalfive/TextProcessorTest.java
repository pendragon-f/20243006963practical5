package com.example.practicalfive;

import org.junit.Test;
import static org.junit.Assert.*;

public class TextProcessorTest {
    private final TextProcessor processor = new TextProcessor();

    @Test public void normalisesSupportedNames() {
        assertEquals("Beijing", processor.processMunicipality("Beijing"));
        assertEquals("Beijing", processor.processMunicipality("beijing"));
        assertEquals("Beijing", processor.processMunicipality("BEIJING"));
        assertEquals("Shanghai", processor.processMunicipality("SHANGHAI"));
        assertEquals("Tianjin", processor.processMunicipality("tianjin"));
        assertEquals("Chongqing", processor.processMunicipality("ChOnGqInG"));
    }

    @Test public void ignoresSurroundingWhitespace() {
        assertEquals("Shanghai", processor.processMunicipality("  Shanghai  "));
        assertEquals("Tianjin", processor.processMunicipality("\tTianjin\n"));
    }

    @Test public void rejectsNullBlankAndUnknownInput() {
        assertNull(processor.processMunicipality(null));
        assertNull(processor.processMunicipality(""));
        assertNull(processor.processMunicipality("   "));
        assertNull(processor.processMunicipality("\t\n"));
        assertNull(processor.processMunicipality("Guangzhou"));
        assertNull(processor.processMunicipality("Bei jing"));
        assertNull(processor.processMunicipality("Beijing Shanghai"));
    }
}
