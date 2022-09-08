# Contributing to the Re3gistry software

> Thank you for your interest in contributing to the Re3gistry project. 
> 
> Please read the information on this page carefully to propagate your changes as soon as possible.

**Please read and make sure you follow our [Code of Conduct](CODE_OF_CONDUCT.adoc) before you start interacting with the Re3gistry community.**

Note that we distinguish between two types of changes to the Re3gistry software that are handled differently:

* For changes that provide bug fixes, a Pull Request can be created referencing an existing bug in the Re3gistry repository.
* Changes including improvements to the software, must first be discussed in a Re3gistry Improvement Proposal (IP) and reference an accepted IP.

The technical managers will review your Pull Request.


## Developer Certificate of Origin

The Re3gistry software is licensed under the [European Public License 1.2](https://opensource.org/licenses/EUPL-1.2). For all project contributions, it is necessary to follow the **Developer Certificate of Origin (DCO) mechanism**.

> The DCO is a legally binding statement that ensures you are the creator of the contribution and that you allow the Re3gistry project to use your work. The Developer Certificate of Origin can be found at [http://developercertificate.org/](http://developercertificate.org/).

The DCO shall be attached to every contribution made by any developer. In the commit message of the contribution, the developer must add a `Signed-off-by´ statement to agree to the DCO and digitally sign it with a GPG signature.

Please check the following resources to learn how to configure and use your GIT:
* [https://git-scm.com/docs/git-commit#git-commit---signoff](https://git-scm.com/docs/git-commit#git-commit---signoff)
* [https://help.github.com/articles/signing-commits/](https://help.github.com/articles/signing-commits/)

Most GIT clients support adding `Signed-off-by´ to the commit messages but do not support the configuration of signing the commits with GPG. Please check if the user settings commit and GPG are set in your local GIT configuration. An example:
      
```
[user]
  name = John Doe
  email = deo@whatever.com
  signingkey = 420A420FFF
[commit]
  gpgsign = true
[gpg]
  program = /usr/local/bin/gpg
```
    
    
## Pull Request Process

* Please read and accept the Developer Certificate of Origin. All commits have to be **signed-off** and **digitally signed**. Make sure you have configured your GIT client accordingly.

* Fork the repository:
    * Navigate to the Re3gistry project at https://github.com/ec-jrc/re3gistry
    * Click **FORK**
    * GitHub will take you to your own copy/fork of the Re3gistry repository
    
 You have successfully created a Re3gistry repository which only exists on GitHub. To work on the project, you need to clone it on your computer.
    
* Clone a fork
     * In GitHub, navigate to **your fork** of the Re3gistry repository
     * Above the list of files, click **Code**
     * Copy the URL for the repository
     * Open Git Bash or your favourite Git tool
     * Change the working directory to the directory where you want to clone
     * Type `git clone´ and the URL you have copied in one of the previous steps. 
     `git clone https://github.com/your_username/re3gistry`
     * Press *Enter*, and your local clone will be created.
     
* Create a branch:
   * Navigate to your local repository
   * Check that your fork is the "origin" remote
   * Add the project repository as the "upstream remote"
   * Pull the latest changes from upstream into your local repository
   * Create a new branch
   Use `git checkout -b branch_name` to create a new branch and immediately switch to it. 
   The branch name should be 'IP-NUMBER' for a Re3gistry Improvement Proposal, where NUMBER is the GitHub issue number from the Re3gistry repository or 'bug-NUMBER' where NUMBER is the GitHub issue number from the Re3gistry repository.
         
* Make the code changes. Please also check the Requirements for a Pull Request section below.
         
* Implement unit tests and test your changes. Run all unit tests of the module with the gradle test task. **Note**: your unit tests, additional integration and system tests are automatically run by a Continuous Integration system when you create the Pull Request and must also be passed.     
 
* Please do not increase the version number. The managers will decide the version number based on the impact of the change.

* Push to your branch and create a Pull Request in our repository. Describe your design decisions for new features in the Pull Request.

 Start adding your code when ready to submit your changes, stage and commit your changes. 
   * Use `git add .` to tell Git to inlude all your changes in the next commit and, 
   * use `git commit` to take a snapshot of your changes. 
   `git commit -m "short description of the changes"`. You can do as many commits as you wish. 
   * When you are ready with your code, Push to your branch `git push` and create a Pull Request in our repository. Describe your design decisions for new features in the Pull Request.

* Create the Pull Request

When opening a "Pull request", you are making a "request" that the project repository "pull" changes from your fork. You will see that the project repository is listed as the "base repository", and your fork is listed as the "head repository"
  
* Review the Pull Request

* You can continue to add commits to your Pull Request even after opening it. For example, the project maintainers may ask you to make more changes, or you may want to include something you forgot.
  
* Discuss the Pull Request   
  You can use the comment box at the bottom of the Pull Request to address questions that the project maintainer might have.

## Requirements for Pull Requests
1. A Pull Request can be composed of one or multiple commits. 
      *  All changes together must address one high-level concern. 
      * If a Pull Request provides multiple distinct features from different sections and each section addresses a particular concern without addressing one common high-level concern, it will be rejected. 
      * Bad Pull Request examples would be providing: a bugfix and adding a feature or a Pull Request that addresses multiple IPs together.
2. Changes must be traceable in the commit history.
3. Make sure you have added Javadocs if you have added public interfaces.
4. Make sure there are no commented-out code sections.
5. The English language needs to be used in the code and comments.



