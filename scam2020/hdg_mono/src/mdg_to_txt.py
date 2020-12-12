import os
import glob
import re
import pandas as pd
import numpy as np

path = "/Users/Amaral/tools/DoctoralResearch/Clusters/translator"

def get_files(path):
    os.chdir(path)
    extension = 'mdg'
    all_filenames = [i for i in glob.glob('*.{}'.format(extension))]
    return all_filenames

def collect_data(file):
    mdg = pd.read_csv(file,delimiter='\t',names=['first','second','count'])
    mdg.drop(columns='count',inplace=True)
    mdg['packages1'] = (mdg['first'].str.extract('(?:_|-)?((?:src_\w+_|test\w+)?(?:[a-zA-Z0-9]+.(?:java|py)))', expand=True))  
    mdg['classes1'] = (mdg['first'].str.split('/', n=1, expand=True))[1] 
    mdg['packages2'] = (mdg['second'].str.extract('(?:_|-)?((?:src_\w+_|test\w+)?(?:[a-zA-Z0-9]+.(?:java|py)))', expand=True)) 
    mdg['classes2'] = (mdg['second'].str.split('/', n=1, expand=True))[1]   
    print(mdg['packages1'])
    return mdg

def process_classes(mdg):
    df = pd.DataFrame(np.append(mdg['classes1'], mdg['classes2']),columns=['classes'])
    df.insert(loc=0,column=0,value='E')
    df.insert(loc=1,column=1,value='class')
    df.drop_duplicates(ignore_index=True,inplace=True)
    return df 

def process_packages(mdg):
    df = pd.DataFrame(np.append(mdg['packages1'], mdg['packages2']),columns=['packages'])
    df.insert(loc=0,column=0,value='E')
    df.insert(loc=1,column=1,value='package')
    df.drop_duplicates(ignore_index=True,inplace=True)
    df.drop_duplicates(ignore_index=True,inplace=True)
    return df 

def process_dependencies(mdg):
    df = pd.DataFrame(mdg['classes1'])
    df['classes2'] = mdg['classes2']
    df.insert(loc=0,column=0,value='R')
    df.insert(loc=1,column=1,value='depends-to')
    df.drop_duplicates(inplace=True,ignore_index=True)
    return df

def process_inheritance(mdg):
    df = pd.DataFrame(np.append(mdg['classes1'], mdg['classes2']),columns=['classes'])
    df['packages'] = np.append(mdg['packages1'], mdg['packages2'])
    df.insert(loc=0,column=0,value='R')
    df.insert(loc=1,column=1,value='pertains-to')
    df.drop_duplicates(inplace=True,ignore_index=True)
    return df

def main():
    #path = "/Users/Amaral/tools/DoctoralResearch/Clusters/translator/data/mdg_files"
    path = "/Users/Amaral/tools/DoctoralResearch/Clusters/translator/"
    destPath = "/Users/Amaral/tools/DoctoralResearch/Clusters/translator/data/txt_files/"
    #destPath = "/Users/Amaral/tools/DoctoralResearch/Clusters/translator/"
    os.chdir(path)
    list_files = get_files(path)
    for f in list_files:
        mdg = collect_data(f)
        classes = process_classes(mdg)
        packages = process_packages(mdg)
        class_dependencies = process_dependencies(mdg)
        inheritance = (process_inheritance(mdg))
        
        classes.to_csv(destPath + f + '.txt', header=None, index=None, sep=';', mode='a')
        packages.to_csv(destPath + f + '.txt', header=None, index=None, sep=';', mode='a')
        class_dependencies.to_csv(destPath + f + '.txt', header=None, index=None, sep=';', mode='a')
        inheritance.to_csv(destPath + f + '.txt', header=None, index=None, sep=';', mode='a')
        with open(destPath + f + '.txt') as a:
            newText=a.read().replace(';', '; ')
            a.close()
        with open(destPath + f + '.txt', "w") as a:
            a.write(newText)
            a.close()
    # print(inheritance)    
        print(f + ': done!')    

if __name__ == "__main__":
    main()
