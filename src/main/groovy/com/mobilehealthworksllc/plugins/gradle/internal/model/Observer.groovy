package com.mobilehealthworksllc.plugins.gradle.internal.model

import java.util.regex.Pattern;

public class Observer {
    String name
    String filePattern
    List<String> filePatterns
    List<String> owners
    List<String> watchers

    Observer(String name) { this.name = name }
}
