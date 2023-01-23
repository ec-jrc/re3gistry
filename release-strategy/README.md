# Release strategy

## Table of contents

  - [Introduction](#introduction)
  - [Objective and Summary](#objective-and-summary)
  - [Release Planning and Milestones](#release-planning-and-milestones)
  - [Release Delivery](#release-delivery)

## Introduction

This document illustrates the release planning strategy for the Re3gistry software, including all its components (Re3gistry and RoR). The document explains the rationale behind the plan and details the foreseen release dates throughout the year together with their main expected changes. It also lists a number of resources for users to get informed on the future expected changes (future releases) in the Re3gistry and to check in detail the content of each released version (past releases).

## Objective and Summary

The objective of this document is to explain the release planning process for the Re3gistry in an open, clear and transparent way to the community in order to ensure that stable validation criteria are provided and communicated efficiently. The release plan is beneficial to the whole community.

The core of the release plan is **the annual major release** of the Re3gistry software. This release includes all the fixed issues and complete developments until that date. Each year the major release of the Re3gistry software is published two months in advance of the release of the Register Federation Tool (RoR).

In addition to this major release, every year **a number of minor releases** are also published (in January, March and September).

In particular, all the **_breaking changes_** (changed that imply importants modifications in the application) are only included in the major versions, while the **_non-breaking changes_** are included in any version (major or minor). In addition, **_hotfixes_** (i.e. fixes to major bugs or faults) are released as quickly as possible, even creating a new minor release outside the roadmap.

## Release Planning and Milestones

### Re3gistry software:

- **Major releases**: June 202x
- **Minor releases**: December 202x, March 202x, September 202x. Every three months between major releases. 
 
 Annual releases, **plan for 2023**:

- **v2.4.1 - January**: Minor release
- **v2.4.2 - March**: Minor release
- **v2.5.0 - June**: Major release
- **v2.5.1 - September**: Minor release 
- **v2.5.2 - December**: Minor release

### Register Federation Tool (RoR):

- **Major releases**: November 202x
- **Minor releases**: February 202x, May 202x, August 202x. Every three months between major releases.

 Annual releases, **plan for 2023**:

- **v0.1.0 - February/March**: Minor release
- **v0.1.1 - May**: Minor release
- **v0.1.2 - June**: Major release
- **v0.1.3 - August**: Minor release 
- **v0.2.0 - November**: Major release

*Clarify that the first version of the Register Federation Tool (RoR) has been postponed to the first trimester of 2023.

To inform users in advance about when the solution to each issue will be included in the release of the Re3gistry software, each issue is assigned to a specific milestone. Milestones are listed on [this page](https://github.com/ec-jrc/re3gistry/milestones). Once a new version of the Re3gistry software is released, the corresponding milestone is closed and moved to the list of closed milestones.

## Release Delivery

All the information on each of the releases carried out, is available [here](https://github.com/ec-jrc/re3gistry/releases). 

Each release of the Re3gistry software is fully managed and made available to the INSPIRE community through the following set of GitHub artifacts:

- A GitHub milestone, named in the same way as the relevant release at that moment (vx.x.x), and published on the Milestones section of the community repository; the milestone lists the issues whose solutions are included in the corresponding vx.x.x release.

- A GitHub release, named vx.x.x and published on the [Release section](https://github.com/ec-jrc/re3gistry/releases) of the community repository. The release notes include:

  - a list of aproved change proposals
  - a list of fixed issues
  - a list of the enhancements
  - the new documentation produced, if any

- Update of the GitHub issue tracker of the community repository by closing the issues with label "SOLVED" and the corresponding milestone.

- Create announcements in [GitHub Discussions](https://github.com/ec-jrc/re3gistry/discussions) and in [joinup](https://joinup.ec.europa.eu/collection/are3na/solution/re3gistry) related to the release.

