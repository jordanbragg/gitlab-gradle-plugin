package com.mobilehealthworksllc.plugins.gradle.internal.utils

import groovy.swing.SwingBuilder

/**
 * Created by jbragg on 9/23/16.
 */
class DialogUtils {
    def static String promptUserForPushingChanges() {
        def pushChanges
        new SwingBuilder().edt {
            dialog(modal: true, title: 'Problems with creating merge request', alwaysOnTop: true, resizable: false, locationRelativeTo: null, pack: true, show: true) {
                vbox {
                    label(text: "Changes aren't all push to remote, push now? (y/n): ")
                    def input1 = textField(columns: 1, id: 'name')
                    button(defaultButton: true, text: 'OK', actionPerformed: {
                        pushChanges = input1.text;
                        dispose();
                    })
                }
            }
        }
        return pushChanges
    }

    def static String promptUserForPrivateToken() {
        def token
        new SwingBuilder().edt {
            dialog(modal: true, title: 'Missing Private Token', alwaysOnTop: true, resizable: false, locationRelativeTo: null, pack: true, show: true) {
                vbox {
                    label(text: "Private token not set (gitlab_token), provide token: ")
                    def input1 = textField(columns: 75, id: 'token')
                    button(defaultButton: true, text: 'OK', actionPerformed: {
                        token = input1.text;
                        dispose();
                    })
                }
            }
        }
        return token
    }
}
