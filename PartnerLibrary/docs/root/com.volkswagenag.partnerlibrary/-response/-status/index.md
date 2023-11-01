//[root](../../../../index.md)/[com.volkswagenag.partnerlibrary](../../index.md)/[Response](../index.md)/[Status](index.md)

# Status

[JVM]\
public enum [Status](index.md)

Errors that happen during API call

## Entries

| | |
|---|---|
| [SUCCESS](-s-u-c-c-e-s-s/index.md) | [JVM]<br>[SUCCESS](-s-u-c-c-e-s-s/index.md)<br>Success |
| [PERMISSION_DENIED](-p-e-r-m-i-s-s-i-o-n_-d-e-n-i-e-d/index.md) | [JVM]<br>[PERMISSION_DENIED](-p-e-r-m-i-s-s-i-o-n_-d-e-n-i-e-d/index.md)<br>Partner application failed validation and is denied permission to access the APIs |
| [SERVICE_CONNECTION_FAILURE](-s-e-r-v-i-c-e_-c-o-n-n-e-c-t-i-o-n_-f-a-i-l-u-r-e/index.md) | [JVM]<br>[SERVICE_CONNECTION_FAILURE](-s-e-r-v-i-c-e_-c-o-n-n-e-c-t-i-o-n_-f-a-i-l-u-r-e/index.md)<br>Unable to find and connect to the service |
| [SERVICE_COMMUNICATION_FAILURE](-s-e-r-v-i-c-e_-c-o-m-m-u-n-i-c-a-t-i-o-n_-f-a-i-l-u-r-e/index.md) | [JVM]<br>[SERVICE_COMMUNICATION_FAILURE](-s-e-r-v-i-c-e_-c-o-m-m-u-n-i-c-a-t-i-o-n_-f-a-i-l-u-r-e/index.md)<br>Failure in communicating the data to the service |
| [VALUE_NOT_AVAILABLE](-v-a-l-u-e_-n-o-t_-a-v-a-i-l-a-b-l-e/index.md) | [JVM]<br>[VALUE_NOT_AVAILABLE](-v-a-l-u-e_-n-o-t_-a-v-a-i-l-a-b-l-e/index.md)<br>Value not available |
| [INITIALIZATION_FAILURE](-i-n-i-t-i-a-l-i-z-a-t-i-o-n_-f-a-i-l-u-r-e/index.md) | [JVM]<br>[INITIALIZATION_FAILURE](-i-n-i-t-i-a-l-i-z-a-t-i-o-n_-f-a-i-l-u-r-e/index.md)<br>Low level initialization failed |
| [INTERNAL_FAILURE](-i-n-t-e-r-n-a-l_-f-a-i-l-u-r-e/index.md) | [JVM]<br>[INTERNAL_FAILURE](-i-n-t-e-r-n-a-l_-f-a-i-l-u-r-e/index.md)<br>Other internal failure in the partner service |

## Functions

| Name | Summary |
|---|---|
| [valueOf](value-of.md) | [JVM]<br>public static [Response.Status](index.md)[valueOf](value-of.md)([String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)name)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [JVM]<br>public static [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[Response.Status](index.md)&gt;[values](values.md)()<br>Returns an array containing the constants of this enum type, in the order they're declared. This method may be used to iterate over the constants. |
