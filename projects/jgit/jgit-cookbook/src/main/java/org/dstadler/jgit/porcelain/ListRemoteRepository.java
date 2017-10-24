package org.dstadler.jgit.porcelain;

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.util.Collection;
import java.util.Map;


/**
 * Simple snippet which shows how to list heads/tags of remote repositories without
 * a local repository
 *
 * @author dominik.stadler at gmx.at
 */
public class ListRemoteRepository {

    private static final String REMOTE_URL = "https://github.com/github/testrepo.git";

    public static void main(String[] args) throws GitAPIException {
        // then clone
        System.out.println("Listing remote repository " + REMOTE_URL);
        Collection<Ref> refs = Git.lsRemoteRepository()
                .setHeads(true)
                .setTags(true)
                .setRemote(REMOTE_URL)
                .call();

        for (Ref ref : refs) {
            System.out.println("Ref: " + ref);
        }

        final Map<String, Ref> map = Git.lsRemoteRepository()
                .setHeads(true)
                .setTags(true)
                .setRemote(REMOTE_URL)
                .callAsMap();

        System.out.println("As map");
        for (Map.Entry<String, Ref> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Ref: " + entry.getValue());
        }

        refs = Git.lsRemoteRepository()
                .setRemote(REMOTE_URL)
                .call();

        System.out.println("All refs");
        for (Ref ref : refs) {
            System.out.println("Ref: " + ref);
        }
    }
}
