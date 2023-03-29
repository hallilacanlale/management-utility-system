Follows properties at all times:
interface for
interface DiagnosticTest {
enum Status { READY, SAMPLED , POSITIVE, NEGATIVE , INVALID} Status getStatus();
}
1. getDiagnosticTests operations never list more doses than a site’s capacity
2. getDiagnosticTests operations never list more items for the whole lab than the sum of the capacity
of all the sites.
3. Adding sampled tests to a site successfully results in those tests being listed in later
getDiagnosticTests operations.
4. Once a test is positive, negative, or invalid; that test is not listed in later getDiagnosticTests
operations.
5. Each test is listed in one site at most by getDiagnosticTests operations.
6. Tests are never “in-transit” due to move operations (i.e., getDiagnosticTests operations not listing
doses removed from the   site and still not added to the   site). 7. Once the status of a test is observed to be
or   , it
cannot be observed to be anything else from that point on.
8. The current contents of any site can be explained by following the entries in the audit log, by the
order in which they appear in the log.
9. The current state and location of any test can be explained by following the entries in the audit log,
by the order in which they appear in the log.
