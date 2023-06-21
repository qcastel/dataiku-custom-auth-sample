# Dataiku custom auth Sample

Note: This custom auth is not supported by dataiku and serves as an example.

## Build the plugin

- Setup the env variable `DKUINSTALLDIR` to the dataiku install dir
- run `ant jar`


## Install the plugin into a DSS instance

- copy the jar `lib/dss-plugin-dataiku-custom-auth-sample.jar` into the dss instance, in `/data/dataiku/dss_data/lib/java/`
- Restart DSS
- Setup the custom auth properties with the properties:
  - `Custom user authenticator full class name`: `com.dataiku.customauth.SampleCustomAuthenticatorAndUserSupplier`
  - `Custom user supplier full class name`: `com.dataiku.customauth.SampleCustomAuthenticatorAndUserSupplier`
