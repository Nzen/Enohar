
/* see ../../../../../LICENSE for release details */

/** 
Provides classes for imitating the enolib 'feature' of returning
fake elements when a caller requests an element of a specific
name that doesn't exist in the parsed document.
These classes implement Bomb and provide the same api as their
corresponding elements. In this fashion, a caller can get
the FakeField from a section with a name that doesn't exist.
So long as a client only uses optional aspects of the FakeField,
sho'he can proceed unaware. When the client requests a required
aspect, the bomb goes off, throwing the appropriate exception.
*/
package ws.nzen.format.eno.missing;
