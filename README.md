[![Android CI with Gradle](https://github.com/opensrp/opensrp-client-native-form/actions/workflows/ci.yml/badge.svg)](https://github.com/opensrp/opensrp-client-native-form/actions/workflows/ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/opensrp/opensrp-client-native-form/badge.svg?branch=master)](https://coveralls.io/github/opensrp/opensrp-client-native-form?branch=master)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/8f21f77d06bf41bcb2405022a53b4a3c)](https://www.codacy.com/gh/opensrp/opensrp-client-native-form/dashboard)

<p align="center">
  <a href="https://smartregister.atlassian.net/wiki/dashboard.action">
    <img src="https://raw.githubusercontent.com/OpenSRP/opensrp-client/master/opensrp-app/res/drawable-mdpi/login_logo.png" alt="OpenSRP" height="60" />
  </a>
</p>

# OpenSRP Client Native Form

A reusable Android library for building rich, validated forms from JSON. It powers multiple OpenSRP apps and extends the original [Android JSON Form Wizard](https://github.com/vijayrawatsan/android-json-form-wizard).

## Contents
- [Features](#features)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Samples](#samples)
- [Multilingual + Rules](#multilingual--rules)
- [Build From Source](#build-from-source)
- [Usage](#usage)
- [JSON Form Attributes](#json-form-attributes)
- [Developer Documentation](#developer-documentation)
- [Customisations From Android Native JSON Form](#customisations-from-android-native-json-form)
- [Extra Input Field Types](#extra-input-field-types)
- [Media / Notes / Images / Videos](#media-image-or-note-display)
- [Repeating Group](#repeating-group)
- [RDT Capture widget](#rdt-capture-widget)
- [Date picker Y-M-D order](#date-picker-change-yearmonthdate-position-programatically)
- [MultiSelectList Widget](#multiselectlist-widget)
- [Configurability](#configurability)
- [Hidden & Disabled Fields](#hidden--disabled-fields)
- [Multi Language Support (MLS)](#multi-language-support-mls)
- [Contributing](#contributing)
- [License](#license)

## Features
- Define forms in plain JSON, no layouts to build by hand
- Built-in validation: regex, numeric, required, and custom rules
- Constraints: min/max, visibility, skip logic, calculated fields
- OpenMRS/OpenSRP metadata mapping support
- Widgets: text, image capture, checkboxes, spinners, radio, barcode, date, tree, repeating groups, and more
- Multi-language strings and external rules engine support

## Installation

You can consume the library via Maven (if available for your org) or as a local module/AAR.

**Gradle (example coordinates):**
```gradle
repositories {
  mavenCentral()
  maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
  maven { url 'https://jitpack.io' }
  mavenLocal()
}

dependencies {
  implementation 'org.smartregister:android-json-form-wizard:3.1.2-SNAPSHOT'
}
