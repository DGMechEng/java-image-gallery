#!/usr/bin/bash

#This script goes into user data for new instance

export IMAGE_GALLERY_BOOTSTRAP_VERSION="1.0"
aws s3 cp s3://edu.au.cc.image-gallery-configure/ec2-prod-latest-m6.sh ./
/usr/bin/bash ec2-prod-latest-m6.sh
