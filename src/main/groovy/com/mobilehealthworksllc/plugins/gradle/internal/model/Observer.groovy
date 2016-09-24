package com.mobilehealthworksllc.plugins.gradle.internal.model

import java.util.regex.Pattern;

public class Observer {
   String name
   String filePattern
   List<String> owners

    Observer(String name){ this.name = name}
}
