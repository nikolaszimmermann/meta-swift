#
# SPDX-License-Identifier: MIT
#

# Runs the program that ci/test-sdk.sh compiled with the SDK on the target,
# to check that what the SDK produces runs against the Swift runtime of the
# image. SWIFT_SDK_TEST_PROGRAM names the program, see ci/kas/scarthgap.yml.

import os

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.runtime.decorator.package import OEHasPackage


class SwiftSDKTest(OERuntimeTestCase):

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['swift-foundation'])
    def test_swift_sdk_program(self):
        program = self.td.get('SWIFT_SDK_TEST_PROGRAM', '')
        self.assertTrue(os.path.isfile(program),
                        msg='%s not found, run ci/test-sdk.sh first' % program)

        dst = '/tmp/swift-sdk-test'
        status, output = self.target.copyTo(program, dst)
        self.assertEqual(status, 0, msg='Copying %s failed: %s' % (program, output))

        status, output = self.target.run(dst)
        self.target.run('rm -f %s' % dst)
        self.assertEqual(status, 0, msg='%s failed: %s' % (dst, output))
        self.assertIn('Hello from the Swift SDK, 1970-01-01T00:00:00Z', output)
