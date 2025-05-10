@SuppressWarnings("module")
module br.com.pinter.tqdatabase {
    exports br.com.pinter.tqdatabase;
    exports br.com.pinter.tqdatabase.models;
    exports br.com.pinter.tqdatabase.cache to br.com.pinter.tqdatabasetest;
    exports br.com.pinter.tqdatabase.dxwrapper;
    requires static com.sun.jna.platform;
    requires static org.apache.commons.lang3;
}
